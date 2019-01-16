package com.hshar.tesserakt.controller

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.set
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.hshar.tesserakt.exception.ResourceNotFoundException
import com.hshar.tesserakt.model.File
import com.hshar.tesserakt.repository.DealRepository
import com.hshar.tesserakt.repository.FileRepository
import com.hshar.tesserakt.repository.UserRepository
import com.hshar.tesserakt.security.CurrentUser
import com.hshar.tesserakt.security.UserPrincipal
import com.hshar.tesserakt.service.S3AwsService
import com.hshar.tesserakt.service.Web3jService
import com.hshar.tesserakt.type.Status
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api")
class FileManagementController {

    @Autowired
    lateinit var fileRepository: FileRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var dealRepository: DealRepository

    @Autowired
    lateinit var s3AwsService: S3AwsService

    @Autowired
    lateinit var web3jService: Web3jService

    @PostMapping("/fileManager/{dealId}")
    @PreAuthorize("hasAnyRole('LENDER','UNDERWRITER')")
    fun uploadFile(
        @RequestParam("filepond") file: MultipartFile,
        @PathVariable dealId: String,
        @CurrentUser currUser: UserPrincipal
    ): ResponseEntity<String> {

        val fullFileName = "$dealId/${file.originalFilename}"
        val user = userRepository.findByEmail(currUser.email)
        val deal = dealRepository.findOneById(dealId)

        // Make sure user in syndicate.
        if (deal.syndicate.members.none { it.user.id == currUser.id }) {
            return ResponseEntity("{\"status\": \"not authorized\"}", HttpStatus.UNAUTHORIZED)
        }

        // If deal is in session (OPEN) then make the file sensitive
        val sensitive = deal.status == Status.OPEN

        fileRepository.insert(File(fullFileName, user, deal, sensitive))
        when (s3AwsService.putObject(fullFileName, file)) {
            true -> {
                web3jService.addDocumentHash(dealId, file, file.originalFilename!!)
                return ResponseEntity("{\"status\": \"success\"}", HttpStatus.OK)
            }
            false -> return ResponseEntity("{\"status\": \"failed\"}", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PostMapping("/fileManager/{dealId}/{fileName}/makeSensitive")
    @PreAuthorize("hasAnyRole('LENDER','UNDERWRITER')")
    fun makeFileSensitive(
        @PathVariable dealId: String,
        @PathVariable fileName: String,
        @CurrentUser currUser: UserPrincipal
    ): ResponseEntity<String> {

        val fullFileName = "$dealId/$fileName"
        val file = fileRepository.findOneByFileName(fullFileName)

        // Make sure user is owner
        if (file.owner.id == currUser.id) {
            file.sensitive = true
            fileRepository.save(file)
            return ResponseEntity("{\"status\": \"success\"}", HttpStatus.OK)
        } else {
            return ResponseEntity("{\"status\": \"not authorized\"}", HttpStatus.UNAUTHORIZED)
        }
    }

    @GetMapping("/fileManager/{dealId}/{fileName}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    @PreAuthorize("hasAnyRole('LENDER','UNDERWRITER')")
    fun getFile(
        @PathVariable dealId: String,
        @PathVariable fileName: String,
        @CurrentUser currUser: UserPrincipal
    ): ResponseEntity<ByteArray> {

        val fullFileName = "$dealId/$fileName"
        val deal = dealRepository.findOneById(dealId)
        val file = fileRepository.findOneByFileName(fullFileName)

        // Make sure user in syndicate or file is not sensitive
        if (deal.syndicate.members.none { it.user.id == currUser.id } ||
                !file.sensitive) {
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }

        val content = s3AwsService.getObject(fullFileName)
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, fileName).body(content)
    }

    @GetMapping("/fileManager/{dealId}")
    @PreAuthorize("hasAnyRole('LENDER','UNDERWRITER')")
    fun getFiles(@PathVariable dealId: String, @CurrentUser currUser: UserPrincipal): ResponseEntity<String> {

        val deal = dealRepository.findOneById(dealId)
        val summaries = s3AwsService.listObjects(dealId)
        val fullFileDataList = Gson().fromJson<JsonArray>(Gson().toJson(summaries.objectSummaries))

        // If user isn't in syndicate, return only non-sensitive files.
        if (deal.syndicate.members.none { it.user.id == currUser.id }) {
            val iterator = fullFileDataList.iterator()
            while (iterator.hasNext()) {
                val fileData = iterator.next()
                val file = fileRepository.findById(fileData["key"].asString)
                    .orElseThrow {
                        ResourceNotFoundException("${fileData["key"]} not found in database.", "key", fileData["key"])
                    }

                if (file.sensitive)
                    iterator.remove()
                else
                    fileData["owner"] = Gson().toJsonTree(file.owner)
            }
        } else {
            fullFileDataList.forEach {
                val file = fileRepository.findById(it["key"].asString)
                    .orElseThrow {
                        ResourceNotFoundException("${it["key"]} not found in database.", "key", it["key"])
                    }

                it["owner"] = Gson().toJsonTree(file.owner)
                it["sensitive"] = file.sensitive
            }
        }
        return ResponseEntity.ok().body(Gson().toJson(fullFileDataList))
    }

    @DeleteMapping("/fileManager/{dealId}/{filename}")
    @PreAuthorize("hasAnyRole('LENDER','UNDERWRITER')")
    fun deleteFile(
        @PathVariable dealId: String,
        @PathVariable filename: String,
        @CurrentUser currUser: UserPrincipal
    ): ResponseEntity<String> {

        val fullFileName = "$dealId/$filename"
        val deal = dealRepository.findOneById(dealId)

        val file = fileRepository.findById(fullFileName)
                .orElseThrow { ResourceNotFoundException("$fullFileName not found in database.", "key", fullFileName) }

        // Make sure user in syndicate and (user owns the file OR user is underwriter)
        if (deal.syndicate.members.any { it.user.id == currUser.id }
                && (file.owner.id == currUser.id || currUser.id == deal.underwriter.id)) {

            when (s3AwsService.deleteObject(fullFileName)) {
                true -> {
                    fileRepository.delete(file)
                    web3jService.removeDocumentHash(dealId, filename)
                }
                false -> return ResponseEntity("{\"status\": \"failed\"}", HttpStatus.INTERNAL_SERVER_ERROR)
            }
            return ResponseEntity("{\"status\": \"success\"}", HttpStatus.OK)
        } else {
            return ResponseEntity("{\"status\": \"not authorized\"}", HttpStatus.UNAUTHORIZED)
        }
    }
}

package com.hshar.tesserakt.controller

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.set
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.hshar.tesserakt.Exception.ResourceNotFoundException
import com.hshar.tesserakt.model.File
import com.hshar.tesserakt.repository.DealRepository
import com.hshar.tesserakt.repository.FileRepository
import com.hshar.tesserakt.repository.UserRepository
import com.hshar.tesserakt.security.CurrentUser
import com.hshar.tesserakt.security.UserPrincipal
import com.hshar.tesserakt.service.S3AwsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
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

    @PostMapping("/fileManager/{dealId}")
    @PreAuthorize("hasRole('LENDER','UNDERWRITER')")
    fun uploadFile(
        @RequestParam("filepond") file: MultipartFile,
        @PathVariable dealId: String,
        @CurrentUser currUser: UserPrincipal): ResponseEntity<String> {

        val fullFileName = "$dealId/${file.originalFilename}"

        val user = userRepository.findByEmail(currUser.email)
        val deal = dealRepository.findOneById(dealId)

        // TODO: Make sure user in syndicate.
        fileRepository.insert(File(fullFileName, user, deal))

        when (s3AwsService.putObject(fullFileName, file)) {
            true -> return ResponseEntity("{\"status\": \"success\"}", HttpStatus.OK)
            false -> return ResponseEntity("{\"status\": \"failed\"}", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/fileManager/{dealId}/{fileName}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    @PreAuthorize("hasRole('LENDER','UNDERWRITER')")
    fun getFile(
        @PathVariable dealId: String,
        @PathVariable fileName: String,
        @CurrentUser currUser: UserPrincipal): ResponseEntity<ByteArray> {

        val fullFileName = "$dealId/$fileName"
        val deal = dealRepository.findOneById(dealId)
        // TODO: Make sure user in syndicate.

        if (fileRepository.existsById(fullFileName)) {
            val content = s3AwsService.getObject(fullFileName)
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, fileName).body(content)
        }

        return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @GetMapping("/fileManager/{dealId}")
    @PreAuthorize("hasRole('LENDER','UNDERWRITER')")
    fun getFiles(
        @PathVariable dealId: String,
        @CurrentUser currUser: UserPrincipal): ResponseEntity<String> {

        val deal = dealRepository.findOneById(dealId)
        // TODO: Make sure user in syndicate.

        val summaries = s3AwsService.listObjects(dealId)
        val fullFileDataList = Gson().fromJson<JsonArray>(Gson().toJson(summaries.objectSummaries))

        fullFileDataList.forEach {
            val file = fileRepository.findById(it["key"].asString)
                .orElseThrow{ ResourceNotFoundException("${it["key"]} not found in database.", "key", it["key"]) }
            it["owner"] = Gson().toJsonTree(file.owner)
        }

        return ResponseEntity.ok().body(Gson().toJson(fullFileDataList))
    }

    @DeleteMapping("/fileManager/{dealId}/{filename}")
    @PreAuthorize("hasRole('LENDER','UNDERWRITER')")
    fun deleteFile(
        @PathVariable dealId: String,
        @PathVariable filename: String,
        @CurrentUser currUser: UserPrincipal): ResponseEntity<String> {

        val fullFileName = "$dealId/$filename"
        val deal = dealRepository.findOneById(dealId)

        val file = fileRepository.findById(fullFileName)
            .orElseThrow{ ResourceNotFoundException("$fullFileName not found in database.", "key", fullFileName) }

        // Make sure user in syndicate and (user owns the file OR user is underwriter)
        if (deal.syndicate.members.any { it.user.id == currUser.id }
            && (file.owner.id == currUser.id || currUser.id == deal.underwriter.id)) {

            when (s3AwsService.deleteObject(fullFileName)) {
                true -> fileRepository.delete(file)
                false -> return ResponseEntity("{\"status\": \"failed\"}", HttpStatus.INTERNAL_SERVER_ERROR)
            }
            return ResponseEntity("{\"status\": \"success\"}", HttpStatus.OK)

        } else {
            return ResponseEntity("{\"status\": \"not authorized\"}", HttpStatus.UNAUTHORIZED)
        }
    }
}

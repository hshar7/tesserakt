//package com.hshar.tesserakt.config
//
//import com.hshar.tesserakt.service.KafkaService
//import org.apache.kafka.clients.consumer.ConsumerConfig
//import org.apache.kafka.clients.producer.ProducerConfig
//import org.apache.kafka.common.serialization.StringDeserializer
//import org.apache.kafka.common.serialization.StringSerializer
//import org.junit.Rule
//import org.springframework.boot.test.context.TestConfiguration
//import org.springframework.context.annotation.Bean
//import org.springframework.kafka.annotation.EnableKafka
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
//import org.springframework.kafka.core.*
//import org.springframework.kafka.test.context.EmbeddedKafka
//import org.springframework.kafka.test.rule.KafkaEmbedded
//import java.util.*
//
//@EnableKafka
//@TestConfiguration
//@EmbeddedKafka(topics = [KafkaService.newDealTopic])
//class KafkaTestConfig {
//
//    @Rule @JvmField
//    final val kafkaEmbeded = KafkaEmbedded(1, false, KafkaService.newDealTopic)
//
//
//    @Bean
//    fun producerFactory(): ProducerFactory<String, String> {
//        val configProps = HashMap<String, Any>()
//        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaEmbeded.brokersAsString
//        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
//        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
//        return DefaultKafkaProducerFactory(configProps)
//    }
//
//    @Bean
//    fun kafkaTemplate(): KafkaTemplate<String, String> {
//        return KafkaTemplate(producerFactory())
//    }
//
//    @Bean
//    fun consumerFactory(): ConsumerFactory<String, String> {
//        val props = HashMap<String, Any>()
//        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaEmbeded.brokersAsString
//        props[ConsumerConfig.GROUP_ID_CONFIG] = "tesserakt1111"
//        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
//        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
//        return DefaultKafkaConsumerFactory(props)
//    }
//
//    @Bean
//    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
//
//        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
//        factory.consumerFactory = consumerFactory()
//        return factory
//    }
//}

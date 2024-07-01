import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.SQSEvent
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import java.io.BufferedReader
import java.io.InputStreamReader
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class SQSConsumer : RequestHandler<SQSEvent, String> {

    private val s3Client: S3Client = S3Client.builder().build()
    private val client = OkHttpClient()
    private val apiUrl = "Receiver_Lambda_URL"
    override fun handleRequest(sqsEvent: SQSEvent, context: Context): String {

        for (record in sqsEvent.records) {
            val message = record.body
//            println("Received message: $message")
            var fileContent: String
            val (bucketName, key) = parseMessage(message)

            try {
                val getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build()

                s3Client.getObject(getObjectRequest).use { s3Object ->
                    val reader = BufferedReader(InputStreamReader(s3Object))
                    fileContent = reader.readText()
                    reader.close()
                    println("File content: $fileContent")
                }
                val processedFinalData = mappingOfColumns(fileContent)
                sendToReceiverLambda(processedFinalData)
            } catch (e: Exception) {
                return "Error retrieving file from S3: ${e.message}"
            }
        }
        return "Processing completed"
    }

    private fun parseMessage(message: String): Pair<String, String> {
        // Parse JSON string into a Map
        val mapper = jacksonObjectMapper()
        val jsonMap: Map<String, Any> = mapper.readValue(message)

        // Access bucket and key values
        val bucketName: String = jsonMap["bucket"].toString()
        val key: String = jsonMap["key"].toString()
        println("Bucket is $bucketName and key is $key")
        return Pair(bucketName, key)
    }

    private fun mappingOfColumns(fileContents: String): String {
        val fileHeaderList = fileContents.split("\n")[0].split(",").toMutableList()
        fileHeaderList[0] = "ID"
        fileHeaderList[1] = "Name"
        fileHeaderList[2] = "DOB"
        val finalHeader = fileHeaderList.joinToString(",")
        return fileContents.replace(
            fileContents.split("\n")[0].split(",").toMutableList().joinToString(","),
            finalHeader
        )
    }

    private fun sendToReceiverLambda(data: String) {
        val json = """{"message": "$data"}"""
        val body = json.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(apiUrl)
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                println("Message sent successfully")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Failed to send message: ${e.message}")
        }
    }
}
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import com.amazonaws.services.sqs.model.SendMessageRequest
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*
class App : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private val s3Client: AmazonS3 = AmazonS3ClientBuilder.defaultClient()
    private val sqsClient: AmazonSQS = AmazonSQSClientBuilder.defaultClient()
    private val bucketName = "bucket_1"
    private val queueName = "queue_1"
    override fun handleRequest(input: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        val headers = mapOf(
            "Content-Type" to "application/json",
            "X-Custom-Header" to "application/json"
        )
        val response = APIGatewayProxyResponseEvent().withHeaders(headers)
        try {
            val fileContent = extractFileContent(input.body)
            println("File content is $fileContent")
            // Convert fileContent to InputStream
            val inputStream: InputStream = ByteArrayInputStream(fileContent.toByteArray())
            // Upload CSV content to S3
            val key = "documents/file3.csv" // Replace with your desired S3 object key
            val metadata = ObjectMetadata()
            val messageBody = """
                {
                    "bucket": "$bucketName",
                    "key": "$key",
                    "metadata": "${metadata.rawMetadata}"
                }
            """.trimIndent()
            sendMessageToSQS(queueName, messageBody)
            metadata.contentType = "text/csv" // Set the content type as per your CSV format
            metadata.contentLength = fileContent.length.toLong()
            s3Client.putObject(bucketName, key, inputStream, metadata)
            val output = "{ \"message\": \"CSV file uploaded to S3\", \"bucket\": \"$bucketName\", \"key\": \"$key\", \"metadata\": \"$metadata\" }"
            return response
                .withStatusCode(200)
                .withBody(output)
        } catch (e: Exception) {
            val errorMessage = "Error occurred: ${e.message}"
            println(errorMessage)
            return response
                .withStatusCode(500)
                .withBody("{ \"error\": \"$errorMessage\" }")
        }
    }
    private fun extractFileContent(body: String): String {
        val parts = body.split("\r\n")
        println("parts is $parts")
        var contentEndIndex = parts.lastIndex - 1
        // Join all parts from contentStartIndex to contentEndIndex to get the file content
        return parts.subList(4, contentEndIndex).joinToString("\r\n")
    }
    private fun sendMessageToSQS(queueName: String, messageBody: String) {
        val queueUrl = sqsClient.getQueueUrl(queueName).queueUrl
        println("queue url is $queueUrl")
        val request = SendMessageRequest()
            .withQueueUrl(queueUrl)
            .withMessageBody(messageBody)
        sqsClient.sendMessage(request)
    }
}

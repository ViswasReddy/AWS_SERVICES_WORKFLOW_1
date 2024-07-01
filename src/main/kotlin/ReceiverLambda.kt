import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.Table
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.GetItemRequest
import com.amazonaws.services.dynamodbv2.model.GetItemResult
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.ObjectMetadata
import com.google.gson.Gson
import java.io.ByteArrayInputStream
import java.math.BigDecimal
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

data class Employee(val id: String, val name: String, val dob: String, var sal: Int)
data class Message(val message: String)
private const val bucketName = "bucket_1"
private val s3Client: AmazonS3 = AmazonS3ClientBuilder.defaultClient()

class ReceiverLambda : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private val gson = Gson()
    private val dynamoDBClient = AmazonDynamoDBClientBuilder.standard().build()
    private val dynamoDB = DynamoDB(dynamoDBClient)
    private val tableName = "table_1"
    private val table: Table = dynamoDB.getTable(tableName)

    override fun handleRequest(input: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        val message = gson.fromJson(input.body, Message::class.java)
        val scanResult = scanTable()
        val employees = parseEmployees(message.message)
        print("Employees are $employees")
        print("message.message is ${message.message}")

        employees.forEach {
            val empId = it.id
            it.sal += scanResult[empId] ?: 0
        }
        val key = "documents/updated1.csv"
        val updatedMessage = formatEmployees(employees)
        println("Updated message: $updatedMessage")
        //csv
        val metadata = ObjectMetadata()
        s3Client.putObject(bucketName, key, ByteArrayInputStream(updatedMessage.toByteArray()), metadata)
        println("Message sent to s3 bucket")
        return APIGatewayProxyResponseEvent().withStatusCode(200).withBody(updatedMessage)
    }

    private fun parseEmployees(message: String): List<Employee> {
        val lines = message.split("\n").drop(1) // Skip the header line
        return lines.map { line ->
            val parts = line.split(",")
            Employee(parts[0], parts[1], parts[2], parts[3].trim().toInt())
        }
    }

    private fun formatEmployees(employees: List<Employee>): String {
        val header = "ID,Name,DOB,sal"
        val lines = employees.joinToString("\n") { employee ->
            "${employee.id},${employee.name},${employee.dob},${employee.sal}"
        }
        return "$header\n$lines"
    }
    private fun scanTable(): MutableMap<String, Int> {
        var searchMap = mutableMapOf<String, Int>()
        val scanRequest = table.scan()
        val iterator = scanRequest.iterator()

        while (iterator.hasNext()) {
            val item = iterator.next()
            val convertedItem = item.asMap()
            searchMap[convertedItem["id"].toString()] = (convertedItem["bonus"] as BigDecimal).toInt()
        }

        return searchMap
    }

}
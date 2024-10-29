import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.exception.outcomes.WrongAnswer;
import org.hyperskill.hstest.stage.StageTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.hyperskill.hstest.testing.TestedProgram;
import org.junit.AfterClass;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hyperskill.hstest.testing.expect.Expectation.expect;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.isObject;

public class JsonDatabaseTest extends StageTest<String> {
    private static final String OK_STATUS = "OK";
    private static final String ERROR_STATUS = "ERROR";
    private static final String NO_SUCH_KEY_REASON = "No such key";

    private static final String WRONG_EXIT = "The server should stop when client sends 'exit' request";

    private static final String clientDataPath = System.getProperty("user.dir") + File.separator +
        "src" + File.separator +
        "client" + File.separator +
        "data";
    private static final String dbFilePath = System.getProperty("user.dir") + File.separator +
        "src" + File.separator +
        "server" + File.separator +
        "data";

    private static final String dbFileName = "db.json";

    private static int threadsCount;

    @DynamicTest(order = 1)
    CheckResult checkExit() {
        TestedProgram server = getServer();
        server.startInBackground();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String serverOutput = server.getOutput().trim();
        if (!serverOutput.toLowerCase().contains("Server started!".toLowerCase())) {
            return CheckResult.wrong("Server output should be 'Server started!'");
        }

        stopServer();

        if (!server.isFinished()) {
            server.stop();
            return CheckResult.wrong(WRONG_EXIT);
        }

        if (!Files.exists(Path.of(dbFilePath + File.separator + dbFileName))) {
            return CheckResult.wrong("Can't find /server/data/db.json file.");
        }

        try (FileWriter fileWriter = new FileWriter(new File(dbFilePath + File.separator + dbFileName))) {
            fileWriter.write("{}");
        } catch (IOException e) {
            return CheckResult.wrong("Close the db.json file before starting the tests.");
        }

        if (!Files.exists(Paths.get(clientDataPath))) {
            return CheckResult.correct();
        }

        String setRequest = JsonBuilder.newBuilder()
            .addValue("type", "set")
            .addValue("key", "name")
            .addValue("value", "Sorabh")
            .getAsJsonObject().toString();

        String getRequest = JsonBuilder.newBuilder()
            .addValue("type", "get")
            .addValue("key", "name")
            .getAsJsonObject().toString();

        String deleteRequest = JsonBuilder.newBuilder()
            .addValue("type", "delete")
            .addValue("key", "name")
            .getAsJsonObject().toString();

        try {
            Files.write(Paths.get(clientDataPath + File.separator + "testSet.json"), setRequest.getBytes());
            Files.write(Paths.get(clientDataPath + File.separator + "testGet.json"), getRequest.getBytes());
            Files.write(Paths.get(clientDataPath + File.separator + "testDelete.json"), deleteRequest.getBytes());
        } catch (IOException e) {
            throw new WrongAnswer("Can't create test files in /client/data/ folder.");
        }

        return CheckResult.correct();
    }

    @DynamicTest(order = 2)
    CheckResult checkDataFolder() {
        if (!Files.exists(Paths.get(clientDataPath))) {
            return CheckResult.wrong("Can't find /client/data/ folder.");
        }

        return CheckResult.correct();
    }

    @DynamicTest(order = 3)
    CheckResult testInputs() throws InterruptedException {
        threadsCount = getThreadCount();

        TestedProgram server = getServer();
        server.startInBackground();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TestedProgram client;
        String output;
        String expectedValue;

        client = getClient();
        output = client.start("-t", "get", "-k", "name");

        String requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "get")
                .value("key", "name")
            );
        String responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", ERROR_STATUS)
                .value("reason", NO_SUCH_KEY_REASON)
            );


        client = getClient();
        output = client.start("-t", "set", "-k", "name", "-v", "Sorabh Tomar");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "set")
                .value("key", "name")
                .value("value", "Sorabh Tomar")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", OK_STATUS)
            );


        client = getClient();
        output = client.start("-t", "set", "-k", "name", "-v", "Sorabh");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "set")
                .value("key", "name")
                .value("value", "Sorabh")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", OK_STATUS)
            );


        client = getClient();
        output = client.start("-t", "get", "-k", "name");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "get")
                .value("key", "name")
            );
        expectedValue = "Sorabh";
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", OK_STATUS)
                .value("value", expectedValue)
            );


        client = getClient();
        output = client.start("-t", "delete", "-k", "name");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "delete")
                .value("key", "name")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", OK_STATUS)
            );


        client = getClient();
        output = client.start("-t", "delete", "-k", "name");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "delete")
                .value("key", "name")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", ERROR_STATUS)
                .value("reason", NO_SUCH_KEY_REASON)
            );


        client = getClient();
        output = client.start("-t", "get", "-k", "name");
        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "get")
                .value("key", "name")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", ERROR_STATUS)
                .value("reason", NO_SUCH_KEY_REASON)
            );


        client = getClient();
        output = client.start("-t", "set", "-k", "text", "-v", "Hyperskill is the best!");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "set")
                .value("key", "text")
                .value("value", "Hyperskill is the best!")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", OK_STATUS)
            );


        client = getClient();
        output = client.start("-t", "get", "-k", "text");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "get")
                .value("key", "text")
            );
        expectedValue = "Hyperskill is the best!";
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", OK_STATUS)
                .value("value", expectedValue)
            );


        client = getClient();
        output = client.start("-t", "get", "-k", "56");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "get")
                .value("key", "56")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", ERROR_STATUS)
                .value("reason", NO_SUCH_KEY_REASON)
            );


        client = getClient();
        output = client.start("-t", "delete", "-k", "56");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "delete")
                .value("key", "56")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", ERROR_STATUS)
                .value("reason", NO_SUCH_KEY_REASON)
            );


        client = getClient();
        output = client.start("-t", "delete", "-k", "100");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "delete")
                .value("key", "100")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", ERROR_STATUS)
                .value("reason", NO_SUCH_KEY_REASON)
            );

        client = getClient();
        output = client.start("-t", "delete", "-k", "That key doesn't exist");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "delete")
                .value("key", "That key doesn't exist")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", ERROR_STATUS)
                .value("reason", NO_SUCH_KEY_REASON)
            );

        client = getClient();
        output = client.start("-in", "testSet.json");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "set")
                .value("key", "name")
                .value("value", "Sorabh")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", OK_STATUS)
            );


        client = getClient();
        output = client.start("-in", "testGet.json");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "get")
                .value("key", "name")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", OK_STATUS)
                .value("value", "Sorabh")
            );

        client = getClient();
        output = client.start("-in", "testDelete.json");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "delete")
                .value("key", "name")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", OK_STATUS)
            );

        client = getClient();
        output = client.start("-in", "testGet.json");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "get")
                .value("key", "name")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", ERROR_STATUS)
                .value("reason", NO_SUCH_KEY_REASON)
            );

        checkIfThreadWasCreated();

        stopServer();

        return CheckResult.correct();
    }

    private static TestedProgram getClient() {
        return new TestedProgram("client");
    }

    private static TestedProgram getServer() {
        return new TestedProgram("server");
    }

    private static void stopServer() {
        TestedProgram client = getClient();
        client.start("-t", "exit");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static int getThreadCount() {
        return (int) ManagementFactory.getThreadMXBean().getTotalStartedThreadCount();
    }

    private static void checkIfThreadWasCreated() {
        int total = getThreadCount();

         // 18 threads: 1 server thread and 17 client threads created during the test.
         // If the server doesn't handle clients in a new thread then the difference between number of threads before and after the test should be equal 18.
        if (total - threadsCount == 18) {
            throw new WrongAnswer("Looks like you don't process client connection in another thread.\n" +
                "Every client request should be parsed and handled in a separate thread!");
        }
    }

    @AfterClass
    public static void deleteFiles() {
        try {
            Files.delete(Paths.get(clientDataPath + File.separator + "testSet.json"));
            Files.delete(Paths.get(clientDataPath + File.separator + "testGet.json"));
            Files.delete(Paths.get(clientDataPath + File.separator + "testDelete.json"));
        } catch (IOException ignored) {
        }
    }
}

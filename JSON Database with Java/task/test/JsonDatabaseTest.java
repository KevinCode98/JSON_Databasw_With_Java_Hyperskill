import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.stage.StageTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.hyperskill.hstest.testing.TestedProgram;

import static org.hyperskill.hstest.testing.expect.Expectation.expect;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.isObject;

public class JsonDatabaseTest extends StageTest<String> {
    private static final String OK_STATUS = "OK";
    private static final String ERROR_STATUS = "ERROR";
    private static final String NO_SUCH_KEY_REASON = "No such key";
    private static final String WRONG_EXIT = "The server should stop when client sends 'exit' request";

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

        return CheckResult.correct();
    }

    @DynamicTest(order = 2)
    CheckResult testInputs() {
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
        output = client.start("-t", "get", "-k", "1");

        String requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "get")
                .value("key", "1")
            );
        String responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", ERROR_STATUS)
                .value("reason", NO_SUCH_KEY_REASON)
            );


        client = getClient();
        output = client.start("-t", "set", "-k", "1", "-v", "Hello world!");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "set")
                .value("key", "1")
                .value("value", "Hello world!")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", OK_STATUS)
            );


        client = getClient();
        output = client.start("-t", "set", "-k", "1", "-v", "HelloWorld!");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "set")
                .value("key", "1")
                .value("value", "HelloWorld!")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", OK_STATUS)
            );


        client = getClient();
        output = client.start("-t", "get", "-k", "1");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "get")
                .value("key", "1")
            );
        expectedValue = "HelloWorld!";
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", OK_STATUS)
                .value("value", expectedValue)
            );


        client = getClient();
        output = client.start("-t", "delete", "-k", "1");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "delete")
                .value("key", "1")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", OK_STATUS)
            );


        client = getClient();
        output = client.start("-t", "delete", "-k", "1");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "delete")
                .value("key", "1")
            );
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
            .asJson()
            .check(isObject()
                .value("response", ERROR_STATUS)
                .value("reason", NO_SUCH_KEY_REASON)
            );


        client = getClient();
        output = client.start("-t", "get", "-k", "1");
        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
            .asJson()
            .check(isObject()
                .value("type", "get")
                .value("key", "1")
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
        output = client.start("-t", "get", "-k", "name");

        requestJson = JsonFinder.findRequestJsonObject(output);
        expect(requestJson)
                .asJson()
                .check(isObject()
                        .value("type", "get")
                        .value("key", "name")
                );
        expectedValue = "Sorabh Tomar";
        responseJson = JsonFinder.findResponseJsonObject(output);
        expect(responseJson)
                .asJson()
                .check(isObject()
                        .value("response", OK_STATUS)
                        .value("value", expectedValue)
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
}

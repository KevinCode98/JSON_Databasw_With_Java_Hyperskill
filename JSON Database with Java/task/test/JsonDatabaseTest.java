import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.stage.StageTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.hyperskill.hstest.testing.TestedProgram;

public class JsonDatabaseTest extends StageTest<String> {
    private static final String OK_STATUS = "OK";
    private static final String ERROR_STATUS = "ERROR";

    private static final String WRONG_EXIT = "The server should stop when client sends 'exit' request";
    private static final String WRONG_GET_EMPTY_CELL_WITH_ERROR = "When a client tries to get an empty cell from the server, the server should send '" + ERROR_STATUS + "' as response and the client should print that response";
    private static final String WRONG_SET_VALUE_TO_CELL_WITH_OK = "When a client tries to save a value on the server, the server should save the value and send '" + OK_STATUS + "' as response. The client should print that response";
    private static final String WRONG_GET_VALUE = "When a client tries to get the value in a non-empty cell from the server, the server should send the value in that non-empty cell as response. And the client should print that received value.\nMaybe the problem is in processing 'set' action: If the specified cell already contains information, you should simply overwrite it with new value.";
    private static final String WRONG_DELETE = "When a client tries to delete a value from the specified cell on the server, the server should assign an empty string to this cell and send '" + OK_STATUS + "' as response.";
    private static final String WRONG_DELETE_EMPTY = "When a client tries to delete a cell with empty value from the server, the server should assign an empty string to that cell and send '" + OK_STATUS + "' as response.";
    private static final String WRONG_DELETE_INDEX_OUT_OF_BOUNDS = "When a user tries to delete a cell whose index is out of bounds (less than 0 or greater than 1000), the server should send '" + ERROR_STATUS + "' as response.";

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
        output = client.start("-t", "get", "-i", "1");
        if (!output.toUpperCase().contains(ERROR_STATUS)) {
            return CheckResult.wrong(WRONG_GET_EMPTY_CELL_WITH_ERROR);
        }

        client = getClient();
        output = client.start("-t", "set", "-i", "1", "-m", "Hello world!");
        if (!output.toUpperCase().contains(OK_STATUS)) {
            return CheckResult.wrong(WRONG_SET_VALUE_TO_CELL_WITH_OK);
        }

        client = getClient();
        output = client.start("-t", "set", "-i", "1", "-m", "HelloWorld!");
        if (!output.toUpperCase().contains(OK_STATUS)) {
            return CheckResult.wrong(WRONG_SET_VALUE_TO_CELL_WITH_OK);
        }

        client = getClient();
        output = client.start("-t", "get", "-i", "1");
        expectedValue = "HelloWorld!";
        if (!output.contains(expectedValue)) {
            return CheckResult.wrong(WRONG_GET_VALUE +
                    "\nExpected:\n" + expectedValue + "\n\nYour output:\n" + output);
        }

        client = getClient();
        output = client.start("-t", "delete", "-i", "1");
        if (!output.toUpperCase().contains(OK_STATUS)) {
            return CheckResult.wrong(WRONG_DELETE);
        }

        client = getClient();
        output = client.start("-t", "delete", "-i", "1");
        if (!output.toUpperCase().contains(OK_STATUS)) {
            return CheckResult.wrong(WRONG_DELETE_EMPTY);
        }

        client = getClient();
        output = client.start("-t", "get", "-i", "1");
        if (!output.toUpperCase().contains(ERROR_STATUS)) {
            return CheckResult.wrong(WRONG_GET_EMPTY_CELL_WITH_ERROR + "\nMaybe after deleting the value from the specified cell on the server, you didn't assign an empty string to it.");
        }

        client = getClient();
        output = client.start("-t", "set", "-i", "55", "-m", "Hyperskill is the best!");
        if (!output.toUpperCase().contains(OK_STATUS)) {
            return CheckResult.wrong(WRONG_SET_VALUE_TO_CELL_WITH_OK);
        }

        client = getClient();
        output = client.start("-t", "get", "-i", "55");
        expectedValue = "Hyperskill is the best!";
        if (!output.contains(expectedValue)) {
            return CheckResult.wrong(WRONG_GET_VALUE +
                    "\nExpected:\n" + expectedValue + "\nYour output:\n" + output);
        }

        client = getClient();
        output = client.start("-t", "get", "-i", "56");
        if (!output.toUpperCase().contains(ERROR_STATUS)) {
            return CheckResult.wrong(WRONG_GET_EMPTY_CELL_WITH_ERROR);
        }

        client = getClient();
        output = client.start("-t", "delete", "-i", "55");
        if (!output.toUpperCase().contains(OK_STATUS)) {
            return CheckResult.wrong(WRONG_DELETE);
        }

        client = getClient();
        output = client.start("-t", "delete", "-i", "56");
        if (!output.toUpperCase().contains(OK_STATUS)) {
            return CheckResult.wrong(WRONG_DELETE_EMPTY);
        }

        client = getClient();
        output = client.start("-t", "delete", "-i", "100");
        if (!output.toUpperCase().contains(OK_STATUS)) {
            return CheckResult.wrong(WRONG_DELETE_EMPTY);
        }

        client = getClient();
        output = client.start("-t", "delete", "-i", "0");
        if (!output.toUpperCase().contains(ERROR_STATUS)) {
            return CheckResult.wrong(WRONG_DELETE_INDEX_OUT_OF_BOUNDS);
        }

        client = getClient();
        output = client.start("-t", "delete", "-i", "1001");
        if (!output.toUpperCase().contains(ERROR_STATUS)) {
            return CheckResult.wrong(WRONG_DELETE_INDEX_OUT_OF_BOUNDS);
        }

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

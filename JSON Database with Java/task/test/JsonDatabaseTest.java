import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.stage.StageTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.hyperskill.hstest.testing.TestedProgram;
import org.hyperskill.hstest.testing.expect.Expectation;

public class JsonDatabaseTest extends StageTest<String> {
    private static final String CORRECT_SERVER_OUTPUT =
        "Server started!\n" +
            "Received: Give me a record # %d\n" +
            "Sent: A record # %d was sent!";

    private static final String CORRECT_CLIENT_OUTPUT =
        "Client started!\n" +
            "Sent: Give me a record # %d\n" +
            "Received: A record # %d was sent!";

    @DynamicTest(order = 1)
    CheckResult test() {
        TestedProgram server = new TestedProgram("server");
        server.startInBackground();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String serverOutput = server.getOutput().trim();
        if (!serverOutput.toLowerCase().equals("Server started!".toLowerCase())) {
            return CheckResult.wrong("Server output should be 'Server started!' until a client connects!");
        }

        TestedProgram client = new TestedProgram("client");
        String clientOutput = client.start();
        serverOutput += "\n" + server.getOutput();

        String[] serverOutputLines = serverOutput.split("\n");
        if (serverOutputLines.length != 3) {
            return CheckResult.wrong("After the client connects to the server, the server output should contain 3 lines!");
        }

        String[] clientOutputLines = clientOutput.split("\n");
        if (clientOutputLines.length != 3) {
            return CheckResult.wrong("After the client connects to the server, the client output should contain 3 lines!");
        }

        int n = Expectation.expect(clientOutputLines[1]).toContain(1).integers().get(0);

        String serverOutputLastLine = serverOutputLines[serverOutputLines.length - 1].trim();
        Expectation.expect(serverOutputLastLine).toContain(1).integers();
        if (!serverOutputLastLine.toLowerCase().contains("Sent: A record #".toLowerCase()) || !serverOutputLastLine.toLowerCase().contains("was sent!")) {
            return CheckResult.wrong("Server output after client connects to the server should be:\n"
                + String.format(CORRECT_SERVER_OUTPUT, n, n) + "\n\nYour output:\n" + serverOutput);
        }

        String clientOutputLastLine = clientOutputLines[clientOutputLines.length - 1].trim();
        Expectation.expect(clientOutputLastLine).toContain(1).integers();
        if (!clientOutputLastLine.toLowerCase().contains("Received: A record #".toLowerCase()) || !clientOutputLastLine.toLowerCase().contains("was sent!")) {
            return CheckResult.wrong("Client output after client connects to the server should be:\n"
                + String.format(CORRECT_CLIENT_OUTPUT, n, n) + "\n\nYour output:\n" + clientOutput);
        }

        return CheckResult.correct();
    }
}

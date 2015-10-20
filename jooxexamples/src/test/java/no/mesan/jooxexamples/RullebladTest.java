package no.mesan.jooxexamples;

import org.joox.Match;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

import static org.joox.JOOX.*;

/**
 * The test is the example.
 * Put it here so that people can run "mvn test" and run the code
 * even if they are to lazy to use and IDE.
 */
public class RullebladTest {

    @Test
    public final void testIt() throws IOException, SAXException {
        Document document = $(new File("src/main/resources/rulleblad.xml")).document();

        // voldsforbrytere som er ustabile bør holdes unna vanlige fanger
        $(document).find("rulleblad")
                   .filter(ctx -> $(ctx).children("dommer").children("dom").attr("type").equals("vold"))
                   .filter(ctx -> $(ctx).children("psyke").content().equals("ustabil"))
                   .append("<avdeling>farlig</avdeling>"); // "append" legger på dette for hver voldsforbryter

        // sexualforbrytere får bo sammen med ustabile voldsmenn: de trenger noe å avreagere på?
        $(document).find("dom")
                   .filter(ctx -> $(ctx).attr("type").equals("sexovergrep"))
                   .parent()
                   .after("<avdeling>farlig</avdeling>");

        // Restrerende fordeles annenhver på avdeling A og avdeling B
        Match resten = $(document).find("rulleblad")
                .filter(ctx -> $(ctx).children("avdeling").isEmpty());

        resten.filter(even()).append("<avdeling>A</avdeling>");

        resten.filter(odd()).append("<avdeling>B</avdeling>");

        String alleA = $(document).find("rulleblad")
                .filter(ctx -> $(ctx).children("avdeling").content().equals("A"))
                .map(ctx -> $(ctx).attr("id"))
                .stream()
                .collect(Collectors.joining(";"));

        //sendTilAvdelingA(alleA);

        // Ferdig, vi visualiserer på enkleste måte med sysout:
        System.out.println($(document).toString());
        System.out.println("\nPersnr eksportert til avdeling A: " + alleA);
    }
}

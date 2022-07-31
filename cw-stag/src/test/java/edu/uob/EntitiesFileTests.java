package edu.uob;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Edge;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class EntitiesFileTests {

  // Test to make sure that the basic entities file is readable
  @Test
  void testBasicEntitiesFileIsReadable() {
      try {
          Parser parser = new Parser();
          FileReader reader = new FileReader("config" + File.separator + "basic-entities.dot");
          parser.parse(reader);
          Graph wholeDocument = parser.getGraphs().get(0);
          ArrayList<Graph> sections = wholeDocument.getSubgraphs();

          // The locations will always be in the first subgraph, the second is paths.
          ArrayList<Graph> locations = sections.get(0).getSubgraphs();
          //先拿第一個 location (cluster001)，底下的 firstLocation 會是 cluster001 整個區塊。
          Graph firstLocation = locations.get(0);

          Node locationDetails = firstLocation.getNodes(false).get(0);// locationDetails = cabin [description="An empty room"];
          //System.out.println(locationDetails);

          //如何拿到 subgraph 本身的名稱，例如：cluster001 等。
          String subgraphName = firstLocation.getId().getId();
          //System.out.println("subgraphName:" + subgraphName);

          //拿 artefacts 整塊 (Sub graphs)
          Graph Details2 = firstLocation.getSubgraphs().get(0);
          //System.out.println("Details2:" + Details2);

          //如何拿到 subgraph 是什麼 type，例如：artefacts/ furniture 等
          String subgraphType = Details2.getId().getId();
          //System.out.println("subgraphType:" + subgraphType);

          //把 artefacts 的內容物抓出來 = potion [description="Magic potion"];
          Node artefactDetail = Details2.getNodes(false).get(0);
          //抓 artefacts 的名字 = potion 。
          String artefactName = artefactDetail.getId().getId();
          //抓 artefacts 的 description = Magic potion 。
          String artefactDescript = artefactDetail.getAttribute("description");
          //System.out.println(artefactDescript);

          // Yes, you do need to get the ID twice !
          String locationName = locationDetails.getId().getId();
          assertEquals("cabin", locationName, "First location should have been 'cabin'");

          // The paths will always be in the second subgraph
          ArrayList<Edge> paths = sections.get(1).getEdges();
          Edge firstPath = paths.get(0);
          Node fromLocation = firstPath.getSource().getNode();
          String fromName = fromLocation.getId().getId();
          Node toLocation = firstPath.getTarget().getNode();
          String toName = toLocation.getId().getId();
          assertEquals("cabin", fromName, "First path should have been from 'cabin'");
          assertEquals("forest", toName, "First path should have been to 'forest'");

      } catch (FileNotFoundException fnfe) {
          fail("FileNotFoundException was thrown when attempting to read basic entities file");
      } catch (ParseException pe) {
          fail("ParseException was thrown when attempting to read basic entities file");
      }
  }

}

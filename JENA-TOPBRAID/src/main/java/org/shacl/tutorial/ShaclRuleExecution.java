package org.shacl.tutorial;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.topbraid.shacl.rules.RuleUtil;
import org.topbraid.jenax.util.JenaUtil;

public class ShaclRuleExecution {
    private static final Logger logger = LoggerFactory.getLogger(ShaclValidation.class);
    // Why This Failure marker
    private static final Marker WTF_MARKER = MarkerFactory.getMarker("WTF");


    public static void main(String[] args) {
        try {
            // Get absolute directory path of this file
            Path path = Paths.get(".").toAbsolutePath().normalize();
            // Create path for data graph
            String data = "file:" + path.toFile().getAbsolutePath() +
                    "/src/main/resources/rectangles.ttl";
            // Create path for shape graph
            String shape = "file:" + path.toFile().getAbsolutePath() +
                    "/src/main/resources/rectangleRules.ttl";

            // Create default model and load data graph
            Model dataModel = JenaUtil.createDefaultModel();
            dataModel.read(data);
            // Create default model and load shape graph
            Model shapeModel = JenaUtil.createDefaultModel();
            shapeModel.read(shape);
            // Prepare the default model graph for inference result
            Model inferenceModel = JenaUtil.createDefaultModel();

            // Create an inference of data graph from shape graph
            inferenceModel = RuleUtil.executeRules(dataModel, shapeModel,
                    inferenceModel, null);

            // Prepare the output file for the result of the inference
            String inferences = path.toFile().getAbsolutePath() +
                    "/src/main/resources/inferences.ttl";
            File inferencesFile = new File(inferences);
            inferencesFile.createNewFile();
            OutputStream reportOutputStream = new FileOutputStream(inferencesFile);

            // Write the result of inference into the desired output file
            RDFDataMgr.write(reportOutputStream, inferenceModel, RDFFormat.TTL);
        } catch (Throwable t) {
            logger.error(WTF_MARKER, t.getMessage(), t);
        }
    }
}
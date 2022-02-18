package org.shacl.tutorial;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.topbraid.shacl.validation.ValidationUtil;
import org.topbraid.shacl.vocabulary.SH;
import org.topbraid.jenax.util.JenaUtil;

public class ShaclValidation {
    private static final Logger logger = LoggerFactory.getLogger(ShaclValidation.class);
    // Why This Failure marker
    private static final Marker WTF_MARKER = MarkerFactory.getMarker("WTF");


    public static void main(String[] args) {
        try {
            // Get absolute directory path of this file
            Path path = Paths.get(".").toAbsolutePath().normalize();
            // Create path for data graph
            String data = "file:" + path.toFile().getAbsolutePath() + "/src/main/resources/exampleGender.ttl";
            // Create path for shape graph
            String shape = "file:" + path.toFile().getAbsolutePath() + "/src/main/resources/exampleGenderShape.ttl";

            // Create default model and load data graph
            Model dataModel = JenaUtil.createDefaultModel();
            dataModel.read(data);
            // Create default model and load shape graph
            Model shapeModel = JenaUtil.createDefaultModel();
            shapeModel.read(shape);

            // Validate the data graph over shape model
            Resource reportResource = ValidationUtil.validateModel(dataModel, shapeModel, true);
            // Get the value of conformation from a validation
            boolean conforms  = reportResource.getProperty(SH.conforms).getBoolean();
            logger.trace("Conforms = " + conforms);

            // Prepare the output file for the result of the validation
            String report = path.toFile().getAbsolutePath() + "/src/main/resources/report.ttl";
            File reportFile = new File(report);
            reportFile.createNewFile();
            OutputStream reportOutputStream = new FileOutputStream(reportFile);

            // Write the validation report into the desired output file
            RDFDataMgr.write(reportOutputStream, reportResource.getModel(), RDFFormat.TTL);

        } catch (Throwable t) {
            logger.error(WTF_MARKER, t.getMessage(), t);
        }
    }
}
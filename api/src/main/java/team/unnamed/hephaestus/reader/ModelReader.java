package team.unnamed.hephaestus.reader;

import team.unnamed.hephaestus.model.Model;

import java.io.File;
import java.io.IOException;

public interface ModelReader {

    Model read(File folder) throws IOException;

}
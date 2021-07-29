package team.unnamed.hephaestus.reader;

import team.unnamed.hephaestus.model.Model;

import java.io.IOException;
import java.io.Reader;

public interface ModelReader {

    Model read(String name, Reader reader) throws IOException;

}
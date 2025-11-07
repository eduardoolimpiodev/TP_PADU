package com.userprocessor.processor.impl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.userprocessor.dto.UserDto;
import com.userprocessor.enums.FileType;
import com.userprocessor.exception.FileProcessingException;
import com.userprocessor.processor.BaseFileProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class XmlFileProcessor extends BaseFileProcessor {

    @Override
    public FileType getSupportedFileType() {
        return FileType.XML;
    }

    @Override
    public List<UserDto> processFile(MultipartFile file) throws Exception {
        validateFileFormat(file);

        List<UserDto> users = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file.getInputStream());
            document.getDocumentElement().normalize();

            Element rootElement = document.getDocumentElement();
            
            if (!"users".equals(rootElement.getNodeName())) {
                throw new FileProcessingException("XML root element must be 'users'");
            }

            NodeList userNodes = rootElement.getElementsByTagName("user");
            
            if (userNodes.getLength() == 0) {
                throw new FileProcessingException("No user elements found in XML");
            }

            for (int i = 0; i < userNodes.getLength(); i++) {
                Node userNode = userNodes.item(i);
                
                if (userNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element userElement = (Element) userNode;
                    
                    String name = getElementTextContent(userElement, "name");
                    String email = getElementTextContent(userElement, "email");

                    if ((name == null || name.trim().isEmpty()) && 
                        (email == null || email.trim().isEmpty())) {
                        continue;
                    }

                    UserDto userDto = new UserDto(
                        name != null ? name.trim() : "",
                        email != null ? email.trim() : ""
                    );

                    validateUserData(userDto, i + 1);
                    users.add(userDto);
                }
            }

        } catch (Exception e) {
            if (e instanceof FileProcessingException) {
                throw e;
            }
            throw new FileProcessingException("Error processing XML file: " + e.getMessage(), e);
        }

        return users;
    }

    private String getElementTextContent(Element parentElement, String tagName) {
        NodeList nodeList = parentElement.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            if (node != null) {
                return node.getTextContent();
            }
        }
        return null;
    }

    @Override
    public void validateFileFormat(MultipartFile file) throws Exception {
        super.validateFileFormat(file);

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file.getInputStream());
            
            Element rootElement = document.getDocumentElement();
            if (!"users".equals(rootElement.getNodeName())) {
                throw new FileProcessingException("XML root element must be 'users'");
            }
            
        } catch (Exception e) {
            if (e instanceof FileProcessingException) {
                throw e;
            }
            throw new FileProcessingException("Invalid XML format: " + e.getMessage(), e);
        }
    }
}

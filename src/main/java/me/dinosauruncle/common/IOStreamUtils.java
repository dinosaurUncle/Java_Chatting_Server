package me.dinosauruncle.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.Socket;
import java.util.Base64;
import java.util.Set;

@Component
public class IOStreamUtils {
    private static final Logger logger = LogManager.getLogger(IOStreamUtils.class);
    private Socket socket = null;
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void outputStreamExecute(JSONObject jsonObject){
        String serializeJsonObject = jsonObject.toJSONString();
        byte[] serializedObject;
        try {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                    oos.writeObject(serializeJsonObject);

                    serializedObject = baos.toByteArray();
                }
            }
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(Base64.getEncoder().encodeToString(serializedObject));
        } catch (IOException e) {
            e.printStackTrace();
            //logger.error(e.getMessage());
        }
    }

    public JSONObject inputStreamExecute(){
        JSONObject result = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            String base64Member = objectInputStream.readObject().toString();
            byte[] inputSerialzedMember = Base64.getDecoder().decode(base64Member);
            try (ByteArrayInputStream bais = new ByteArrayInputStream(inputSerialzedMember)) {
                try (ObjectInputStream ois = new ObjectInputStream(bais)) {
                    JSONParser jsonParser = new JSONParser();
                    result = (JSONObject)jsonParser.parse(String.valueOf(ois.readObject()));
                    //logger.info(result);
                }
            }
        } catch (IOException e){
            //logger.error(e.getMessage());
            logger.info("로그아웃");
        } catch (ClassNotFoundException e){
            //logger.error(e.getMessage());
            e.printStackTrace();
        } catch (ParseException e) {
            //logger.error(e.getMessage());
            e.printStackTrace();
        } finally {

        }
        return result;
    }

    public String getKey(JSONObject jsonObject){
        Set<String> keySet = jsonObject.keySet();
        String key = "";
        for (String eachKey : keySet){
            key = eachKey;
        }
        return key;
    }
}

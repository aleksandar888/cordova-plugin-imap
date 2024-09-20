import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import javax.mail.Message;
import javax.mail.internet.MimeBodyPart;
import javax.mail.MessagingException;
import javax.mail.Part;


public class TextBodyPart {

    public JSONObject parse(Message bodyPart) throws MessagingException, JSONException, IOException {
        JSONObject contentData = new JSONObject();

        if (bodyPart.isMimeType("TEXT/calendar")) {
            contentData.put("type", bodyPart.getContentType());
            contentData.put("content", new ProcessStreamContent().decodeStream((InputStream) bodyPart.getContent()));
        } else if (bodyPart.isMimeType("TEXT/html") || bodyPart.isMimeType("TEXT/plain")) {
            contentData.put("type", bodyPart.getContentType());
            contentData.put("content", bodyPart.getContent());
        } else {
            contentData.put("type", bodyPart.getContentType());
        }
        return contentData;
    }

    public JSONObject parse(MimeBodyPart bodyPart) throws MessagingException, JSONException, IOException {
        JSONObject contentData = new JSONObject();

        if (bodyPart.isMimeType("TEXT/calendar")) {
            contentData.put("type", bodyPart.getContentType());
            contentData.put("content", new ProcessStreamContent().decodeStream((InputStream) bodyPart.getContent()));
        } else if (bodyPart.isMimeType("TEXT/html") || bodyPart.isMimeType("TEXT/plain")) {
            contentData.put("type", bodyPart.getContentType());
            contentData.put("content", bodyPart.getContent());
        } else {
            contentData.put("type", bodyPart.getContentType());
        }
        return contentData;
    }

    public JSONObject parse(Part bodyPart) throws MessagingException, JSONException, IOException {
        JSONObject contentData = new JSONObject();

        if (bodyPart.isMimeType("TEXT/calendar")) {
            contentData.put("type", bodyPart.getContentType());
            contentData.put("content", new ProcessStreamContent().decodeStream((InputStream) bodyPart.getContent()));
        } else if (bodyPart.isMimeType("TEXT/html") || bodyPart.isMimeType("TEXT/plain")) {
            contentData.put("type", bodyPart.getContentType());
            contentData.put("content", bodyPart.getContent());
        } else {
            contentData.put("type", bodyPart.getContentType());
        }
        return contentData;
    }
}
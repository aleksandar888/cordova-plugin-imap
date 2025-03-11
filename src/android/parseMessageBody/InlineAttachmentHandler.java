import org.json.JSONObject;

import javax.mail.Multipart;
import javax.mail.Message;
import javax.mail.Part;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;

import com.sun.mail.util.BASE64DecoderStream;

public class InlineAttachmentHandler {

    public byte[] getInlineAttachmentContentData(Message message, String fileName) throws Exception {
        try {
            if (message.isMimeType("multipart/mixed")) {
                return parseMultipartMixedBody((MimeMultipart) message.getContent(), fileName);
            } else if (message.isMimeType("multipart/alternative")) {
                return parseMultipartAlternativeBody((Multipart) message.getContent(), fileName);
            } else if (message.isMimeType("multipart/related")) {
                return parseMultipartRelatedBody((MimeMultipart) message.getContent(), fileName);
            }

            return new byte[0];
        } catch (Exception ex) {
            throw ex;
        }
    }

    private byte[] parseMultipartRelatedBody(MimeMultipart bodyContent, String fileName) throws Exception {
        try {
            for (int index = 0; index < bodyContent.getCount(); index++) {

                byte[] resultData = new byte[0];

                Part bodyPart = bodyContent.getBodyPart(index);

                if (bodyPart.isMimeType("IMAGE/*") && MimeUtility.decodeText(bodyPart.getFileName()).equals(fileName)) {
                    resultData = new ProcessStreamContent().toByteArray((BASE64DecoderStream) bodyPart.getContent());
                } else if (bodyPart.isMimeType("multipart/alternative")) {
                    resultData = parseMultipartAlternativeBody((Multipart) bodyPart.getContent(), fileName);
                }

                if (resultData.length > 0) {
                    return resultData;
                }
            }

            return new byte[0];
        } catch (Exception ex) {
            throw ex;
        }
    }

    private byte[] parseMultipartMixedBody(MimeMultipart bodyContent, String fileName) throws Exception {
        try {
            for (int partCount = 0; partCount < bodyContent.getCount(); partCount++) {
                byte[] resultData = new byte[0];

                MimeBodyPart bodyPart = (MimeBodyPart) bodyContent.getBodyPart(partCount);

                if (bodyPart.isMimeType("IMAGE/*") && MimeUtility.decodeText(bodyPart.getFileName()).equals(fileName)) {
                    resultData = new ProcessStreamContent().toByteArray((BASE64DecoderStream) bodyPart.getContent());
                } else if (bodyPart.isMimeType("multipart/related")) {
                    resultData = parseMultipartRelatedBody((MimeMultipart) bodyPart.getContent(), fileName);
                } else if (bodyPart.isMimeType("multipart/alternative")) {
                    resultData = parseMultipartAlternativeBody((Multipart) bodyPart.getContent(), fileName);
                }

                if (resultData.length > 0) {
                    return resultData;
                }
            }

            return new byte[0];
        } catch (Exception ex) {
            throw ex;
        }
    }

    private byte[] parseMultipartAlternativeBody(Multipart bodyContent, String fileName) throws Exception {
        try {
            for (int index = 0; index < bodyContent.getCount(); index++) {
                JSONObject contentData = new JSONObject();
                byte[] resultData = new byte[0];

                Part bodyPart = bodyContent.getBodyPart(index);

                if (bodyPart.isMimeType("IMAGE/*") && MimeUtility.decodeText(bodyPart.getFileName()).equals(fileName)) {
                    resultData = new ProcessStreamContent().toByteArray((BASE64DecoderStream) bodyPart.getContent());
                } else if (bodyPart.isMimeType("multipart/related")) {
                    resultData = parseMultipartRelatedBody((MimeMultipart) bodyPart.getContent(), fileName);
                }

                if (resultData.length > 0) {
                    return resultData;
                }
            }

            return new byte[0];
        } catch (Exception ex) {
            throw ex;
        }
    }
}
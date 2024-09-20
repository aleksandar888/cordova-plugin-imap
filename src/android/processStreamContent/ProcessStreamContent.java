import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.Base64;

import com.sun.mail.util.BASE64DecoderStream;


public class ProcessStreamContent {

    public static String decodeStream(InputStream stream) {
        return new BufferedReader(new InputStreamReader(stream)).lines().collect(Collectors.joining("\n"));
    }

    public static String readBase64Image(Object bodyPart, String contentType) throws Exception {
        byte[] encode = toByteArray((BASE64DecoderStream) bodyPart);
        return "data:" + getImageType(contentType) + ";base64," + new String(Base64.getEncoder().encode(encode));
    }

    private static byte[] toByteArray(BASE64DecoderStream bodyPartStream) throws Exception {
        byte[] data = new byte[1024];
        int count = bodyPartStream.read(data);
        int startPos = 0;
        while (count == 1024) {
            byte[] addBuffer = new byte[data.length + 1024];
            System.arraycopy(data, 0, addBuffer, 0, data.length);
            startPos = data.length;
            data = addBuffer;
            count = bodyPartStream.read(data, startPos, 1024);
        }
        return data;
    }

    private static String getImageType(String contentType) {
        String[] charsetTypes = new String[]{"image/png", "image/jpeg", "image/gif", "image/svg", "image/tiff", "image/bmp"};
        contentType = contentType.toLowerCase();

        for (String type : charsetTypes) {
            if (contentType.indexOf(type) > -1)
                return type;
        }
        return null;
    }
}
package org.lpw.tephra.poi.pptx;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.lpw.tephra.util.Http;
import org.lpw.tephra.util.Json;
import org.lpw.tephra.util.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
@Component("tephra.poi.pptx.image")
public class ImageParserImpl implements Parser {
    @Inject
    private Http http;
    @Inject
    private Json json;
    @Inject
    private Logger logger;
    @Inject
    private ParserHelper parserHelper;

    @Override
    public String getType() {
        return "image";
    }

    @Override
    public boolean parse(XMLSlideShow xmlSlideShow, XSLFSlide xslfSlide, JSONObject object) {
        String image = object.getString("image");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Map<String, String> map = new HashMap<>();
        http.get(image, null, null, map, outputStream);
        if (map.isEmpty())
            return false;

        try {
            String contenType = map.get("Content-Type");
            XSLFPictureData xslfPictureData = xmlSlideShow.addPicture(parserHelper.getImage(object, contenType, outputStream),
                    getPictureType(image, contenType));
            XSLFPictureShape xslfPictureShape = xslfSlide.createPicture(xslfPictureData);
            xslfPictureShape.setAnchor(parserHelper.getRectangle(object));
            parserHelper.rotate(xslfPictureShape, object);

            return true;
        } catch (IOException e) {
            logger.warn(e, "解析图片[{}]时发生异常！", object.toJSONString());

            return false;
        }
    }

    private PictureData.PictureType getPictureType(String url, String contentType) {
        switch (contentType) {
            case "image/jpeg":
                return PictureData.PictureType.JPEG;
            case "image/gif":
                return PictureData.PictureType.GIF;
            default:
                if (!contentType.equals("image/png"))
                    logger.warn(null, "未处理图片类型[{}:{}]！", url, contentType);
                return PictureData.PictureType.PNG;
        }
    }
}

package cn.torna.common.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码工具类
 *
 * @author qiuyu
 */
@Slf4j
public final class QRCodeUtil {

    private static final int DEFAULT_WIDTH = 280;
    private static final int DEFAULT_HEIGHT = 280;
    private static final int DEFAULT_LOGO_WIDTH = 44;
    private static final int DEFAULT_LOGO_HEIGHT = 44;
    private static final String IMAGE_FORMAT = "png";
    private static final String CHARSET = "utf-8";
    private static final String BASE64_IMAGE_PREFIX = "data:image/png;base64,";
    private static final int LOGO_CORNER_RADIUS = 6;
    private static final float LOGO_STROKE_WIDTH = 3f;

    // 二维码生成参数
    private static final Map<EncodeHintType, Comparable<?>> DEFAULT_HINTS = createDefaultHints();

    private QRCodeUtil() {
    }

    private static Map<EncodeHintType, Comparable<?>> createDefaultHints() {
        Map<EncodeHintType, Comparable<?>> hints = new HashMap<>(3);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 2);
        return hints;
    }

    /**
     * 生成Base64格式的二维码图片(默认尺寸)
     *
     * @param content 二维码内容，不能为空
     * @return Base64编码的图片字符串
     * @throws IllegalArgumentException 如果内容为空
     */
    public static String getBase64QRCode(String content) {
        return getBase64QRCode(content, DEFAULT_WIDTH, DEFAULT_HEIGHT, null, null, null);
    }

    /**
     * 生成带Logo的Base64格式二维码图片(默认尺寸)
     *
     * @param content 二维码内容，不能为空
     * @param logoUrl Logo URL地址
     * @return Base64编码的图片字符串
     * @throws IllegalArgumentException 如果内容为空
     */
    public static String getBase64QRCode(String content, String logoUrl) {
        return getBase64QRCode(content, DEFAULT_WIDTH, DEFAULT_HEIGHT, logoUrl, DEFAULT_LOGO_WIDTH, DEFAULT_LOGO_HEIGHT);
    }

    /**
     * 生成自定义尺寸的Base64格式二维码图片
     *
     * @param content    二维码内容，不能为空
     * @param width      二维码宽度
     * @param height     二维码高度
     * @param logoUrl    Logo URL地址
     * @param logoWidth  Logo宽度
     * @param logoHeight Logo高度
     * @return Base64编码的图片字符串
     * @throws IllegalArgumentException 如果内容为空或尺寸参数无效
     */
    public static String getBase64QRCode(String content, Integer width, Integer height,
                                         String logoUrl, Integer logoWidth, Integer logoHeight) {
        validateContent(content);
        width = validateSize(width, DEFAULT_WIDTH);
        height = validateSize(height, DEFAULT_HEIGHT);
        logoWidth = validateSize(logoWidth, DEFAULT_LOGO_WIDTH);
        logoHeight = validateSize(logoHeight, DEFAULT_LOGO_HEIGHT);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            BufferedImage image = createQRCode(content, width, height, logoUrl, logoWidth, logoHeight);
            if (image != null) {
                ImageIO.write(image, IMAGE_FORMAT, os);
                return BASE64_IMAGE_PREFIX + Base64.getEncoder().encodeToString(os.toByteArray());
            }
        } catch (IOException e) {
            log.error("生成二维码失败", e);
        }
        return null;
    }

    /**
     * 生成二维码图片到输出流(默认尺寸)
     *
     * @param content 二维码内容
     * @param output  输出流
     * @throws IOException 如果写入输出流失败
     * @throws IllegalArgumentException 如果内容为空
     */
    public static void getQRCode(String content, OutputStream output) throws IOException {
        validateContent(content);
        BufferedImage image = createQRCode(content, DEFAULT_WIDTH, DEFAULT_HEIGHT, null, 0, 0);
        if (image != null) {
            ImageIO.write(image, IMAGE_FORMAT, output);
        }
    }

    /**
     * 生成带Logo的二维码图片到输出流(默认尺寸)
     *
     * @param content 二维码内容
     * @param logoUrl Logo URL地址
     * @param output  输出流
     * @throws IOException 如果写入输出流失败
     * @throws IllegalArgumentException 如果内容为空
     */
    public static void getQRCode(String content, String logoUrl, OutputStream output) throws IOException {
        validateContent(content);
        BufferedImage image = createQRCode(content, DEFAULT_WIDTH, DEFAULT_HEIGHT,
                logoUrl, DEFAULT_LOGO_WIDTH, DEFAULT_LOGO_HEIGHT);
        if (image != null) {
            ImageIO.write(image, IMAGE_FORMAT, output);
        }
    }

    /**
     * 创建二维码图片
     */
    private static BufferedImage createQRCode(String content, int width, int height,
                                              String logoUrl, int logoWidth, int logoHeight) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, DEFAULT_HINTS);

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            renderQRCode(bitMatrix, image, width, height);

            if (StringUtils.isNotBlank(logoUrl)) {
                addLogo(image, width, height, logoUrl, logoWidth, logoHeight);
            }
            return image;
        } catch (Exception e) {
            log.error("生成二维码异常", e);
            return null;
        }
    }

    /**
     * 渲染二维码图片
     */
    private static void renderQRCode(BitMatrix bitMatrix, BufferedImage image, int width, int height) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
    }

    /**
     * 添加Logo到二维码
     */
    private static void addLogo(BufferedImage image, int width, int height,
                                String logoUrl, int logoWidth, int logoHeight) throws IOException {
        Image logoImage = ImageIO.read(new URL(logoUrl));
        Graphics2D graphics = image.createGraphics();

        try {
            int x = (width - logoWidth) / 2;
            int y = (height - logoHeight) / 2;

            graphics.drawImage(logoImage, x, y, logoWidth, logoHeight, null);

            Shape roundRect = new RoundRectangle2D.Float(x, y, logoWidth, logoHeight,
                    LOGO_CORNER_RADIUS, LOGO_CORNER_RADIUS);
            graphics.setStroke(new BasicStroke(LOGO_STROKE_WIDTH));
            graphics.draw(roundRect);
        } finally {
            graphics.dispose();
        }
    }

    /**
     * 验证内容是否有效
     */
    private static void validateContent(String content) {
        if (StringUtils.isBlank(content)) {
            throw new IllegalArgumentException("二维码内容不能为空");
        }
    }

    /**
     * 验证尺寸参数是否有效
     */
    private static int validateSize(Integer size, int defaultValue) {
        return size != null && size > 0 ? size : defaultValue;
    }
}

package com.soilmate.barcodecore.parser;

import com.soilmate.barcodecore.model.QRCodeDecodeResult;

import java.awt.image.BufferedImage;
import java.io.InputStream;

public interface BarcodeParser {

    QRCodeDecodeResult parse(BufferedImage image);

    QRCodeDecodeResult parse(InputStream inputStream);

    QRCodeDecodeResult parse(byte[] bytes);

    QRCodeDecodeResult parseBase64(String base64);

}

package com.soilmate.barcodecore.parser;

import com.soilmate.barcodecore.model.QRCodeParseResult;

import java.awt.image.BufferedImage;
import java.io.InputStream;

public interface BarcodeParser {

    QRCodeParseResult parse(BufferedImage image);

    QRCodeParseResult parse(InputStream inputStream);

    QRCodeParseResult parse(byte[] bytes);

    QRCodeParseResult parseBase64(String base64);

}

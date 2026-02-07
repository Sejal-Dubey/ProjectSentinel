package com.sentinel.backend.service;

import de.androidpit.colorthief.ColorThief;
import de.androidpit.colorthief.MMCQ;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@Service
@lombok.extern.slf4j.Slf4j
public class ImageAnalysisService {

    /**
     * Extracts color profile using ColorThief (Java port of Android Palette/MMCQ).
     */
    public ImageProfile analyzeImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return new ImageProfile("#000000", "#777777", 0.0, java.util.Collections.emptyList());
        }

        try {
            BufferedImage image;
            if (imageUrl.startsWith("data:image")) {
                // Base64 Decode
                String base64Data = imageUrl.split(",")[1];
                byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Data);
                try (java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(imageBytes)) {
                    image = ImageIO.read(bis);
                }
            } else {
                // URL Download
                URL url = new URL(imageUrl);
                image = ImageIO.read(url);
            }
            
            if (image == null) return new ImageProfile("#000000", "#777777", 0.0, java.util.Collections.emptyList());

            // Extract Palette (Top 5 Colors)
            MMCQ.CMap result = ColorThief.getColorMap(image, 5);
            List<MMCQ.VBox> palette = result.vboxes;

            String vibrantHex = "#000000";
            String mutedHex = "#777777";
            double maxSaturation = -1.0;
            double minSaturation = 2.0;

            // Simple heuristic to classify colors
            for (MMCQ.VBox swatch : palette) {
                int[] rgb = swatch.avg(false);
                String hex = String.format("#%02X%02X%02X", rgb[0], rgb[1], rgb[2]);
                double[] hsv = rgbToHsv(rgb[0], rgb[1], rgb[2]);
                
                // Vibrant = High Saturation, High Value
                if (hsv[1] > maxSaturation && hsv[1] > 0.3) {
                    maxSaturation = hsv[1];
                    vibrantHex = hex;
                }
                
                // Muted = Low Saturation
                if (hsv[1] < minSaturation) {
                    minSaturation = hsv[1];
                    mutedHex = hex;
                }
            }

            // Collect all hex codes for AI context
            List<String> hexPalette = palette.stream()
                .map(swatch -> {
                    int[] rgb = swatch.avg(false);
                    return String.format("#%02X%02X%02X", rgb[0], rgb[1], rgb[2]);
                })
                .collect(java.util.stream.Collectors.toList());

            // Calculate luminance of the Vibrant color for context
            int[] vibRgb = hexToRgb(vibrantHex);
            double luminance = (0.2126 * vibRgb[0] + 0.7152 * vibRgb[1] + 0.0722 * vibRgb[2]) / 255.0;

            log.info("Visual Pipeline: Extracted Palette {} | Luminance: {}", hexPalette, String.format("%.2f", luminance));

            return new ImageProfile(vibrantHex, mutedHex, luminance, hexPalette);

        } catch (Exception e) {
            System.err.println("Failed to analyze image: " + e.getMessage());
            return new ImageProfile("#000000", "#777777", 0.0, java.util.Collections.emptyList());
        }
    }

    private double[] rgbToHsv(int r, int g, int b) {
        float[] hsv = new float[3];
        java.awt.Color.RGBtoHSB(r, g, b, hsv);
        return new double[]{hsv[0], hsv[1], hsv[2]};
    }

    private int[] hexToRgb(String hex) {
        return new int[]{
            Integer.valueOf(hex.substring(1, 3), 16),
            Integer.valueOf(hex.substring(3, 5), 16),
            Integer.valueOf(hex.substring(5, 7), 16)
        };
    }

    public record ImageProfile(String vibrantHex, String mutedHex, Double luminance, List<String> palette) {
        @Override
        public String toString() {
            return String.format("Palette: %s, Brightness: %.2f", palette, luminance);
        }
    }
}

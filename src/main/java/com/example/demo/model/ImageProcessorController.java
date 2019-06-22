package com.example.demo.model;

import com.example.demo.exceptions.InvalidParametrException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.model.Histogram;
import com.example.demo.model.Image;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.jhlabs.image.GaussianFilter;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class ImageProcessorController {
    public Map<String, BufferedImage> images = new HashMap<>();
    private static int nextID = 0;

    private String generateID() {
        nextID++;
        return Integer.toString(nextID);
    }

    public String setImage(InputStream inputStream) {
        String ID = generateID();
        try {
            if (inputStream != null) {
                BufferedImage bufferedImage = ImageIO.read(inputStream);
                if (bufferedImage != null) images.put(ID, bufferedImage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (images.containsKey(ID)) return Integer.toString(nextID);
        else return null;
    }

    public String generateJSON(String id) {
        if (images.containsKey(id)) {
            int height = images.get(id).getHeight();
            int width = images.get(id).getWidth();
            com.example.demo.model.Image image = new Image(id, height, width);
            return image.toString();
        } else return null;
    }

    public boolean deleteImage(String id) {
        if (images.containsKey(id)) {
            images.remove(id);
            return true;
        } else throw new ResourceNotFoundException("ID not found!");
    }

    public String getSize(String id) {
        if (images.containsKey(id)) {
            BufferedImage tempImage = images.get(id);
            JsonObject object = new JsonObject();
            object.addProperty("height", tempImage.getHeight());
            object.addProperty("width", tempImage.getWidth());
            return object.toString();
        } else throw new ResourceNotFoundException("ID not found!");
    }

    public String returnHistogram(String id) {
        if (images.containsKey(id)) {
            BufferedImage image = images.get(id);
            float tab1[] = new float[256];
            float tab2[] = new float[256];
            float tab3[] = new float[256];
            for (int i = 0; i < 256; i++) {
                tab1[i]=0;
                tab2[i]=0;
                tab3[i]=0;
            }

            for (int i = 0; i < image.getHeight(); i++) {
                for (int j = 0; j < image.getWidth(); j++) {

                    Color c = new Color(image.getRGB(j, i));
                    tab1[c.getRed()] += 1;
                    tab2[c.getGreen()] += 1;
                    tab3[c.getBlue()]+= 1;
                }
            }
            for (int i = 0; i < 256; i++) {
                System.out.println(tab1[i]);
                tab1[i]/=image.getWidth()*image.getHeight();
                tab2[i]/=image.getWidth()*image.getHeight();
                tab3[i]/=image.getWidth()*image.getHeight();
            }

            Histogram histogram = new Histogram(tab1, tab2, tab3);


            ObjectMapper Obj = new ObjectMapper();
            String jsonStr = null;
            try {

                jsonStr = Obj.writeValueAsString(histogram);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return jsonStr;
        } else throw new ResourceNotFoundException("ID not found");
    }

    public byte[] getCroppedImage(String id, int start, int stop, int width, int height) {
        if (images.containsKey(id)) {
            BufferedImage image = images.get(id);
            if (start < image.getWidth() && stop < image.getHeight()) {
                if (start + width < image.getWidth() && stop + height < image.getHeight()) {
                    BufferedImage croppedImage = image.getSubimage(start, stop, width, height);
                    return returnImageInByte(croppedImage);
                } else throw new InvalidParametrException("Out of bounds image!");
            } else throw new InvalidParametrException("Out of bounds image!");
        } else throw new ResourceNotFoundException("ID not found!");
    }

    public byte[] getGrayImage(String id) {
        if (images.containsKey(id)) {
            BufferedImage image = images.get(id);
            return makeImageGray(image);
        } else throw new ResourceNotFoundException("ID not found!");
    }

    private byte[] makeImageGray(BufferedImage image) {
        for (int i = 0; i < image.getHeight(); i++) {

            for (int j = 0; j < image.getWidth(); j++) {

                Color c = new Color(image.getRGB(j, i));
                int red = (int) (c.getRed() * 0.299);
                int green = (int) (c.getGreen() * 0.587);
                int blue = (int) (c.getBlue() * 0.114);
                Color newColor = new Color(red + green + blue, red + green + blue, red + green + blue);

                image.setRGB(j, i, newColor.getRGB());
            }
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            baos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] imageInByte = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageInByte;
    }

    private byte[] returnImageInByte(BufferedImage image) {
        if(image!=null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ImageIO.write(image, "png", baos);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                baos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] imageInByte = baos.toByteArray();
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return imageInByte;
        }
        else throw new ResourceNotFoundException("image is null");
    }

    public byte[] getImageBlurred(String id, double radius) {
        if (images.containsKey(id)) {
            BufferedImage temp = null;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GaussianFilter g = new GaussianFilter();
            g.setRadius((float) radius);
            temp = g.filter(images.get(id), temp);


            try {
                ImageIO.write(temp, "png", baos);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return baos.toByteArray();
        } else throw new ResourceNotFoundException("id not found");
    }

    public boolean checkIfRadiusOK(String rad) {

        try {
            Integer.parseInt(rad);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean checkIfRadiusIsPositive(String rad) {

        if (checkIfRadiusOK(rad)) {

            if (Integer.parseInt(rad) > 0) return true;
            else return false;
        } else return false;
    }

}

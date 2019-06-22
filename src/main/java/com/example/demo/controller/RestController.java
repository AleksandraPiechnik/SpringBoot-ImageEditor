package com.example.demo.controller;

import com.example.demo.model.ImageProcessorController;
import com.example.demo.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@org.springframework.web.bind.annotation.RestController
public class RestController {
    private ImageProcessorController imageProcessorController = new ImageProcessorController();

    @RequestMapping(value = "/image/add", method = RequestMethod.POST)
    public String addImage(HttpServletRequest requestEntity) throws Exception {
        String id = imageProcessorController.setImage(requestEntity.getInputStream());
        if (id != null) return imageProcessorController.generateJSON(id);
        else throw new ResourceNotFoundException("id not found");
    }

    @RequestMapping(value = "/image/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteImage(@PathVariable String id) {
        boolean imageRemoveSucceed = imageProcessorController.deleteImage(id);
        if (imageRemoveSucceed == false) return new ResponseEntity(HttpStatus.NOT_FOUND);
        else return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/image/size/{id}", method = RequestMethod.GET)
    public String getSize(@PathVariable String id) {
        return imageProcessorController.getSize(id);
    }


    @RequestMapping(value = "/image/{id}/crop/{start}/{stop}/{width}/{height}", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getScaledImage(@PathVariable String id, @PathVariable String start, @PathVariable String stop, @PathVariable String width, @PathVariable String height) {
        return imageProcessorController.getCroppedImage(id, Integer.parseInt(start), Integer.parseInt(stop), Integer.parseInt(width), Integer.parseInt(height));
    }

    @RequestMapping(value = "/image/{id}/greyscale", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getGrayImage(@PathVariable String id) {
        return imageProcessorController.getGrayImage(id);
    }

    @RequestMapping(value = "/image/{id}/blur/{radius}", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getImageBlurred(@PathVariable String radius, @PathVariable String id) {
        if (imageProcessorController.checkIfRadiusOK(radius) && imageProcessorController.checkIfRadiusIsPositive(radius)) {
            return imageProcessorController.getImageBlurred(id, Integer.parseInt(radius));
        } else throw new ResourceNotFoundException("Radius is incorrect");
    }

    @RequestMapping(value = "/image/{id}/histogram", method = RequestMethod.GET)
    public String hist(@PathVariable String id) {
        return imageProcessorController.returnHistogram(id);
    }
}

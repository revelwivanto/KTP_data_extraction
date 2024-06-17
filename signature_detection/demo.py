import sys
import os
import cv2
import numpy as np

from src.signature_detect.cropper import Cropper
from src.signature_detect.extractor import Extractor
from src.signature_detect.loader import Loader
from PIL import Image, ImageEnhance

def increase_brightness(img, value=60):
    for _ in range(4):  # Iterate 3 times
        if len(img.shape) == 2:  # Check if the image is grayscale
            img = cv2.cvtColor(img, cv2.COLOR_GRAY2BGR)
        hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
        h, s, v = cv2.split(hsv)

        lim = 255 - value
        v[v > lim] = 255
        v[v <= lim] += value

        final_hsv = cv2.merge((h, s, v))
        img = cv2.cvtColor(final_hsv, cv2.COLOR_HSV2BGR)
        if len(img.shape) == 3 and img.shape[2] == 3:  # Convert back to grayscale if necessary
            img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    return img

def main(file_path: str) -> None:
    # Open the image and convert it to grayscale using PIL
    wbimg = Image.open(file_path).convert('L')
    
    # Save the grayscale image
    base_name = os.path.splitext(os.path.basename(file_path))[0]
    image_filename = os.path.join('data/input', f"wbimg_{base_name}.png")
    wbimg.save(image_filename)
    
    # Read the saved grayscale image with OpenCV
    image = cv2.imread(image_filename, cv2.IMREAD_GRAYSCALE)
    
    # Apply Gaussian blur using OpenCV
    gaussian_blur = cv2.GaussianBlur(image, (7, 7), sigmaX=2)
    
    # Sharpen the image
    wbsharpimg = cv2.addWeighted(image, 1.5, gaussian_blur, -0.5, 0)
    
    # Increase the brightness of the sharpened image
    wbsharpbrightimg = increase_brightness(wbsharpimg, value=30)
    
    # Convert to PIL Image for further enhancements
    wbsharpbrightimg_pil = Image.fromarray(wbsharpbrightimg)
    
    # Increase contrast
    wbsharpbrightimg_pil = ImageEnhance.Contrast(wbsharpbrightimg_pil).enhance(3.0)
    
    # Increase saturation
    wbsharpbrightimg_pil = ImageEnhance.Color(wbsharpbrightimg_pil).enhance(3.0)
    
    # Save the brightened, sharpened, and enhanced image
    bright_sharp_image_filename = os.path.join('data/input', f"wbimgsharpbright_{base_name}.png")
    wbsharpbrightimg_pil.save(bright_sharp_image_filename)
    
    # Continue with the existing pipeline using the enhanced image file
    loader = Loader()
    extractor = Extractor(amplfier=15)
    cropper = Cropper()

    try:
        masks = loader.get_masks(bright_sharp_image_filename)
        for mask in masks:
            labeled_mask = extractor.extract(mask)
            try:
                cropper.run(labeled_mask)
            except Exception as e:
                print(f"Error during cropping and saving: {e}")
    except Exception as e:
        print(e)

if __name__ == "__main__":
    file_path = None
    for i in range(len(sys.argv)):
        if sys.argv[i] == "--file":
            file_path = sys.argv[i + 1]
    if file_path is None:
        print("Need input file")
        print("python demo.py --file my-file.pdf")
    else:
        main(file_path)

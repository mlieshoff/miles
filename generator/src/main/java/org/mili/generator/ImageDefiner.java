package org.mili.generator;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class ImageDefiner {

    public static void main(String[] args) throws IOException {
//        define(new File("/home/micha/back/1"));
        //        define(new File("/home/micha/back/2"));
        //        define(new File("/home/micha/back/3"));
        //        define(new File("/home/micha/back/4"));
        define(new File("/home/micha/back/5"));
    }

    private static void define(File dir) throws IOException {
        for (File file : dir.listFiles()) {
            if (isBlank(FilenameUtils.getExtension(file.getName()))) {
                System.out.println(file.getAbsolutePath());
/*                ImageInputStream iis = ImageIO.createImageInputStream(file);
                Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);
                while (imageReaders.hasNext()) {
                    ImageReader reader = imageReaders.next();
                    FileUtils.copyFile(file, new File(dir, file.getName() + "." + reader.getFormatName().toLowerCase()));
                }
                */
                FileUtils.copyFile(file, new File(dir, file.getName() + ".jpg"));
                FileUtils.deleteQuietly(file);
            }
        }
    }

}

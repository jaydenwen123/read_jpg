package com.homework.test;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.MemoryImageSource;
import java.io.FileInputStream;

import com.homework.decode.JPEGDecoder;
import com.homework.decode.JPEGDecoder.PixelArray;

class Bild extends Frame implements Runnable, JPEGDecoder.PixelArray {

	Image im = null;

	Thread load;

	String file;

	JPEGDecoder jpegDecoder = null;

	// Implementation of PixelArray

	int[] pix;

	int width, height;

	public void setSize(int width, int height) {

		this.width = width;

		this.height = height;

		pix = new int[width * height];

	}

	public void setPixel(int x, int y, int argb) {

		pix[x + y * width] = argb;

	}

	// Image viewer

	public static void main(String args[]) {

		new Bild("C:\\Users\\Administrator\\Desktop\\200px-50.jpg");

	}

	public Bild(String s) {

		file = s;

		jpegDecoder = new JPEGDecoder();

		load = new Thread(this);

		load.start();

		this.setTitle("Bild:" + s);

		this.resize(300, 200);

		this.show();

		while (im == null) {

			try {

				Thread.sleep(1000);

			} catch (Exception e) {
			}

			repaint();

		}

	}

	public void run() {

		try {

			FileInputStream in = new FileInputStream(file);

			jpegDecoder.decode(in, this);

			in.close();

			MemoryImageSource mi = new MemoryImageSource(width,

			height,

			pix,

			0,

			width);

			im = createImage(mi);

		} catch (Exception e) {

			System.out.println("Etwas ging schief: " + e);

		}

	}
	
	public void paint(Graphics g){

        if(im != null){

             g.drawImage(im,0,0,this);

        }else{

     g.drawString("Decodierung",40,50);

             if(jpegDecoder!=null)

                  g.drawString("Progress:"+jpegDecoder.progress()+"%",40,70);

        }

}

	

}
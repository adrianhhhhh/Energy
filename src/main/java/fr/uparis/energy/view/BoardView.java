package fr.uparis.energy.view;

import fr.uparis.energy.model.BoardObservable;
import fr.uparis.energy.model.Component;
import fr.uparis.energy.model.Geometry;
import fr.uparis.energy.model.ReadOnlyTile;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class BoardView extends JPanel implements BoardObserver {
    private BoardObservable rob;

    public BoardView() {
        this.setPreferredSize(new Dimension(800, 800));
        this.setBackground(Color.BLACK);
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0,0,getWidth(),getHeight());
        if (rob == null) return;

        int tileWidth;
        if (this.rob.getGeometry() == Geometry.SQUARE)
            tileWidth = this.getWidth() / this.rob.getWidth();
        else
            tileWidth = 4 * this.getWidth() / (3 * this.rob.getWidth() + 1);

        int tileHeight;
        if (this.rob.getGeometry() == Geometry.SQUARE)
            tileHeight = this.getHeight() / this.rob.getHeight();
        else
            tileHeight = (int)Math.floor(this.getHeight() / (this.rob.getHeight() +  0.5));

        if (this.rob.getGeometry() == Geometry.SQUARE) {
            tileWidth = Math.min(tileWidth, tileHeight);
            tileHeight = tileWidth;
        }

        for (int i = 0; i < rob.getHeight(); i++) {
            for (int j = 0; j < rob.getWidth(); j++) {
                int x;
                if (rob.getGeometry() == Geometry.SQUARE || j == 0) x = j * tileWidth;
                else{
                    x = j * (tileWidth - 35);
                }  
                int y;
                if (rob.getGeometry() == Geometry.HEXAGON && j % 2 == 1) y = i * tileHeight + tileHeight / 2;
                else y = i * tileHeight;
                this.drawTile(g,rob.getTileAt(i,j),x,y,tileWidth,tileHeight);
            }
            
        }
    }

    private void drawTile(Graphics g, ReadOnlyTile rot, int x, int y, int width, int height) {
        g.drawImage(
                SpriteBank.getShape(this.rob.getGeometry(), rot.getPowerState()), x, y,  width, height, null);

        Image component = SpriteBank.getComponent(
                this.rob.getGeometry(), rot.getPowerState(), rot.getComponent());
        g.drawImage(component, x, y, width, height, null);
        if (rot.getGeometry() == Geometry.SQUARE) {
            drawSquareTile(g,rot, x, y, width, height);
        } else {
          drawHexagonTile(g,rot,x,y,width,height);
        }
    }
    
     private void drawSquareTile(Graphics g, ReadOnlyTile rot, int x, int y, int width, int height) {
         if (rot.getComponent() != Component.EMPTY) {
             BufferedImage wire = SpriteBank.getWire(SpriteBank.WireType.SQUARE_SHORT, rot.getPowerState());
             for (int i = 0; i < rot.getGeometry().card(); i++) {

                 if (rot.getConnectorsExist()[i]) {
                     Common.drawRotatedImage(g, x, y, width, height, 90 * i, wire);
                 }
             }
         } else {
             boolean[] c = rot.getConnectorsExist();
             Image straightWire =  SpriteBank.getWire(SpriteBank.WireType.SQUARE_LONG, rot.getPowerState());
             Image curvedWire = SpriteBank.getWire(SpriteBank.WireType.SQUARE_CURVED, rot.getPowerState());


             if (c[0] && !c[1] && c[2] && !c[3]) {
                 Common.drawRotatedImage(g, x, y, width, height, 0, straightWire);
             } else if (!c[0] && c[1] && !c[2] && c[3])
                 Common.drawRotatedImage(g, x, y, width, height, 90, straightWire);
             else {
                 if (c[0] && c[1])
                     Common.drawRotatedImage(g, x, y, width, height, 0,curvedWire);

                 if (c[1] && c[2])
                     Common.drawRotatedImage(g, x, y, width, height, 90, curvedWire);
                 if (c[2] && c[3])
                     Common.drawRotatedImage(g, x, y, width, height, 180, curvedWire);
                 if (c[0] && c[3])
                     Common.drawRotatedImage(g, x, y, width, height, 270, curvedWire);
             }
         }
     }
     
    private  void drawHexagonTile(Graphics g, ReadOnlyTile rot, int x, int y, int width, int height) {
        if (rot.getComponent() != Component.EMPTY) {
            BufferedImage wire = SpriteBank.getWire(SpriteBank.WireType.HEXAGON_SHORT, rot.getPowerState());
            for (int i = 0; i < rot.getGeometry().card(); i++) {

                if (rot.getConnectorsExist()[i]) {
                    Common.drawRotatedImage(g, x, y, width, height, 60 * i, wire);
                }
            }
        } else {
            boolean [] c = rot.getConnectorsExist();
            Image hexagonLongWire = SpriteBank.getWire(SpriteBank.WireType.HEXAGON_LONG,rot.getPowerState());
            Image hexagonCurvedShortWire = SpriteBank.getWire(SpriteBank.WireType.HEXAGON_CURVED_SHORT,rot.getPowerState());
            Image hexagonCurvedLongWire = SpriteBank.getWire(SpriteBank.WireType.HEXAGON_CURVED_LONG,rot.getPowerState());

            if (c[0] && !c[1] && !c[2] && c[3] && !c[4] && !c[5]) Common.drawRotatedImage(g,x,y,width,height,0, hexagonLongWire);
            else if (!c[0] && c[1] && !c[2] && !c[3] && c[4] && !c[5]) Common.drawRotatedImage(g,x,y,width,height,60, hexagonLongWire);
            else if(!c[0] && !c[1] && c[2] && !c[3] && !c[4] && c[5]) Common.drawRotatedImage(g,x,y,width,height,120, hexagonLongWire);
            else {
                for (int i = 0; i < rot.getGeometry().card(); i++) {
                    if (c[i] && c[(i+1) % rot.getGeometry().card()]) Common.drawRotatedImage(g,x,y,width,height,60 * i,hexagonCurvedShortWire);
                    if (c[i] && !c[(i+1) % rot.getGeometry().card()] && c[(i +2) % 6]) Common.drawRotatedImage(g,x,y,width,height,60 * i,hexagonCurvedLongWire);
                }
            }
        }
    }

    @Override
    public void update(BoardObservable boardObservable) {
        this.rob = boardObservable;
        this.repaint();
    }
}

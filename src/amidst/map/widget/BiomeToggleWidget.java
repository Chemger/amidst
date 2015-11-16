package amidst.map.widget;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import amidst.map.MapViewer;
import amidst.map.layer.BiomeLayer;
import amidst.resources.ResourceLoader;

public class BiomeToggleWidget extends PanelWidget {
	private static BufferedImage highlighterIcon = ResourceLoader
			.getImage("highlighter.png");
	public static boolean isBiomeWidgetVisible = false;

	public BiomeToggleWidget(MapViewer mapViewer) {
		super(mapViewer);
		setSize(36, 36);
	}

	@Override
	public void draw(Graphics2D g2d, float time) {
		super.draw(g2d, time);
		g2d.drawImage(highlighterIcon, getX(), getY(), 36, 36, null);
	}

	@Override
	public boolean onMousePressed(int x, int y) {
		isBiomeWidgetVisible = !isBiomeWidgetVisible;
		BiomeLayer.instance.setHighlightMode(isBiomeWidgetVisible);
		(new Thread(new Runnable() {
			@Override
			public void run() {
				map.repaintFragmentsLayer(BiomeLayer.instance.getLayerId());
			}
		})).start();
		return true;
	}
}

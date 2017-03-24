package server.model.objects;

import server.Server;

public class Object {

	public int objectId;
	public int objectX;
	public int objectY;
	public int height;
	public int face;
	public int type;
	public int newId;
	public int tick;
	public boolean remove;

	public Object(int id, int x, int y, int height, int face, int type,
			int newId, int ticks, boolean remove) {
		this.objectId = id;
		this.objectX = x;
		this.objectY = y;
		this.height = height;
		this.face = face;
		this.type = type;
		this.newId = newId;
		this.tick = ticks;
		this.remove = remove;
		Server.objectManager.addObject(this);
	}

	public int getId() {
		return this.objectId;
	}

	public int getX() {
		return this.objectX;
	}

	public int getY() {
		return this.objectY;
	}

	public int getHeight() {
		return this.height;
	}

	public int getFace() {
		return this.face;
	}

	public int getType() {
		return this.type;
	}

	public int getNewId() {
		return this.newId;
	}

	public int getTicks() {
		return this.tick;
	}

	public boolean getRemove() {
		return this.remove;
	}

}
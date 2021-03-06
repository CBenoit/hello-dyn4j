/**********************************************************************
This is free and unencumbered software released into the public domain.

Anyone is free to copy, modify, publish, use, compile, sell, or
distribute this software, either in source code form or as a compiled
binary, for any purpose, commercial or non-commercial, and by any
means.

In jurisdictions that recognize copyright laws, the author or authors
of this software dedicate any and all copyright interest in the
software to the public domain. We make this dedication for the benefit
of the public at large and to the detriment of our heirs and
successors. We intend this dedication to be an overt act of
relinquishment in perpetuity of all present and future rights to this
software under copyright law.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

For more information, please refer to <http://unlicense.org>
**********************************************************************/

package fr.auroden.hellodyn4j;

import org.dyn4j.dynamics.Body;

import java.util.ArrayList;
import java.util.List;

public class World extends org.dyn4j.dynamics.World {
	List<BodyWrapper> bodies = new ArrayList<>();

	// time step for Box2D.
	float timeStep = 1f / 60f;

	// listeners
	private final List<WorldListener> listeners = new ArrayList<>();

	public World() {
		super();
	}

	@Override
	public void addBody(Body newBody) {
		super.addBody(newBody);
		BodyWrapper bodyWrapper = new BodyWrapper(newBody);
		this.bodies.add(bodyWrapper);
		fireBodyAdded(bodyWrapper);
	}

	public void update() {
		update(this.timeStep); // be aware that updatev exists!
		this.bodies.stream()
				.filter(bodyWrapper -> (!bodyWrapper.getBody().isAsleep()))
				.forEach(BodyWrapper::update);
		fireWorldUpdate();
	}

	public void addWorldListener(WorldListener worldListener) {
		assert (worldListener != null);
		this.listeners.add(worldListener);
	}

	public void removeWorldListener(WorldListener worldListener) {
		assert (worldListener != null);
		this.listeners.remove(worldListener);
	}

	private void fireEvent(WorldEvent e) {
		for (final WorldListener worldListener : this.listeners) {
			worldListener.worldUpdate(e);
		}
	}

	private void fireWorldUpdate() {
		final WorldEvent e = new WorldEvent(this, WorldEvent.Type.WORLD_UPDATE);
		fireEvent(e);
	}

	private void fireBodyAdded(BodyWrapper body) {
		final WorldEvent e = new WorldEvent(this, body, WorldEvent.Type.BODY_ADDED);
		fireEvent(e);
	}

	private void fireBodyRemoved(BodyWrapper body) {
		final WorldEvent e = new WorldEvent(this, body, WorldEvent.Type.BODY_REMOVED);
		fireEvent(e);
	}
}

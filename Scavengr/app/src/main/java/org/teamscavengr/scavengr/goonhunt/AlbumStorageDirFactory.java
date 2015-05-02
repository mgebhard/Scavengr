package org.teamscavengr.scavengr.goonhunt;

import java.io.File;import java.lang.String;

abstract class AlbumStorageDirFactory {
	public abstract File getAlbumStorageDir(String albumName);
}

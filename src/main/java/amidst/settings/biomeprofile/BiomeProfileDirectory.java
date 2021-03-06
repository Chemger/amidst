package amidst.settings.biomeprofile;

import java.io.File;
import java.io.IOException;

import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.parsing.FormatException;
import amidst.parsing.json.JsonReader;

@Immutable
public class BiomeProfileDirectory {
	public static BiomeProfileDirectory create(File biomeProfilesDirectory) {
	    if (biomeProfilesDirectory == null) {
	        biomeProfilesDirectory = DEFAULT_ROOT_DIRECTORY;
	    }
		BiomeProfileDirectory result = new BiomeProfileDirectory(biomeProfilesDirectory);
		AmidstLogger.info("using biome profiles at: '" + result.getRoot() + "'");
		return result;
	}

	private static final File DEFAULT_ROOT_DIRECTORY = new File("biome");

	private final File root;
	private final File defaultProfile;

	public BiomeProfileDirectory(File root) {
		this.root = root;
		this.defaultProfile = new File(root, "default.json");
	}

	public File getRoot() {
		return root;
	}

	public File getDefaultProfile() {
		return defaultProfile;
	}

	public boolean isValid() {
		return root.isDirectory();
	}

	public void saveDefaultProfileIfNecessary() {
		if (!isValid()) {
			AmidstLogger.info("Unable to find biome profile directory.");
		} else {
			AmidstLogger.info("Found biome profile directory.");
			if (defaultProfile.isFile()) {
				AmidstLogger.info("Found default biome profile.");
			} else if (BiomeProfile.getDefaultProfile().save(defaultProfile)) {
				AmidstLogger.info("Saved default biome profile.");
			} else {
				AmidstLogger.info("Attempted to save default biome profile, but encountered an error.");
			}
		}
	}

	public void visitProfiles(BiomeProfileVisitor visitor) {
		visitProfiles(root, visitor);
	}

	private void visitProfiles(File directory, BiomeProfileVisitor visitor) {
		boolean entered = false;
		for (File file : directory.listFiles()) {
			if (file.isFile()) {
				BiomeProfile profile = createFromFile(file);
				if (profile != null) {
					if (!entered) {
						entered = true;
						visitor.enterDirectory(directory.getName());
					}
					visitor.visitProfile(profile);
				}
			} else {
				visitProfiles(file, visitor);
			}
		}
		if (entered) {
			visitor.leaveDirectory();
		}
	}

	private BiomeProfile createFromFile(File file) {
		if (file.exists() && file.isFile()) {
			try {
				BiomeProfile profile = JsonReader.readLocation(file, BiomeProfile.class);
				if(profile.validate()) {
					return profile;
				}
				AmidstLogger.warn("Profile invalid, ignoring: {}", file);
			} catch (IOException | FormatException e) {
				AmidstLogger.warn(e, "Unable to load file: {}", file);
			}
		}
		return null;
	}
}

package unconfined;

import lombok.AccessLevel;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.ApiStatus;
import unconfined.api.UnconfinedAPI;

import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.Manifest;

@Log4j2(access = AccessLevel.PUBLIC)
public final class Unconfined {

    private static final String UNCONFINED_STANDALONE_MANIFEST = "Unconfined-Standalone";
    private static final String UNCONFINED_STANDALONE_PROPERTY = "unconfined.standalone";
    private static Boolean standalone;

    /// @see UnconfinedAPI#isStandaloneMode()
    @ApiStatus.Internal
    public static boolean isStandalone() {
        if (standalone == null) {
            try {
                // read the property first, then the manifest
                String optInProp = System.getProperty(UNCONFINED_STANDALONE_PROPERTY);
                if (optInProp != null) {
                    standalone = Boolean.parseBoolean(optInProp);
                } else {
                    standalone = isStandalone0();
                }
            } catch (Exception e) {
                standalone = false;
                log.warn("Failed to load manifest", e);
            }
        }
        return standalone;
    }

    /// Load the [#UNCONFINED_STANDALONE_MANIFEST] from the `MANIFEST.MF` in this JAR.
    private static boolean isStandalone0() throws Exception {
        String classname = Unconfined.class.getSimpleName() + ".class";
        URL classUrl = Unconfined.class.getResource(classname);
        if (classUrl == null) {
            log.warn("Failed to load manifest from null classUrl");
            return false;
        }
        String urlString = classUrl.toString();
        if (!urlString.startsWith("jar:")) {
            log.warn("Failed to load manifest from non-JAR url {}", urlString);
            return false;
        }
        JarURLConnection conn = (JarURLConnection) classUrl.openConnection();
        Manifest manifest = conn.getManifest();
        String optInValue = manifest.getMainAttributes().getValue(UNCONFINED_STANDALONE_MANIFEST);
        return Boolean.parseBoolean(optInValue);
    }

}

package sanchay.server.utils;

import sanchay.server.SanchayServerApplication;
import sanchay.server.dto.auth.model.domain.SanchayUserDTO;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

public class SanchayServerUtils {

    private static final Properties applicationProperties;

    static {
        applicationProperties = new Properties();

        try {
            ClassLoader classLoader = SanchayServerUtils.class.getClassLoader();
            InputStream applicationPropertiesStream = classLoader.getResourceAsStream("application.properties");
            applicationProperties.load(applicationPropertiesStream);
        } catch (Exception e) {
            // process the exception
        }
    }

    public static String getApplicationProperty(String propertyName)
    {
        return applicationProperties.getProperty(propertyName);
    }

    public static Properties loadPropertiesFile(String propertiesPath) {
        Properties properties = null;
        try (InputStream input = new FileInputStream(propertiesPath)) {

            properties = new Properties();

            // load a properties file
            properties.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return properties;
    }

    public static Object getFirstMapKey(Map map)
    {
        Map.Entry<Object,Object> entry = (Map.Entry<Object,Object>) map.entrySet().iterator().next();
        String key = (String) entry.getKey();
//        String value = entry.getValue();

        return key;
    }

    public static void printEnvironmentVariables()
    {
        Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            System.out.format("%s=%s%n",
                    envName,
                    env.get(envName));
        }
    }

    public static String buildServerAnnotationFolderPath(SanchayUserDTO user)
    {
        String path = SanchayServerApplication.SANCHAY_CONFIG_PROPERTIES.getProperty("SHARED_ANNOTATION_FOLDER");

        String organisationName = user.getCurrentOrganisation().getName();

        String languageName = user.getCurrentLanguage().getName();

        String annotationLevel = user.getCurrentAnnotationLevel().getName();

        String username = user.getUsername();

        path = path + "/annotation/" + organisationName + "/" + languageName
                + "/" + annotationLevel + "/" + "/" + username;

        path = Paths.get(path).normalize().toString();

        File file = new File(path);

        if(!file.exists()) {
            file.mkdirs();
        }

        return path;
    }

    public static String buildRelativePath(String longPath, String shortPath)
    {
        Path pathAbsolute = Paths.get(longPath);
        Path pathBase = Paths.get(shortPath);
        Path pathRelative = pathBase.relativize(pathAbsolute);

        return pathRelative.normalize().toString();
    }
}

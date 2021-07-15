package cn.torna.swaggerplugin.scaner;
import java.io.IOException;
import java.util.List;

public interface PackageScanner {
    List<String> getFullyQualifiedClassNameList() throws IOException;
}
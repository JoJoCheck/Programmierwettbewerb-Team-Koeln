package gui.filehandler;

import java.util.List;

public record FolderInfo(FolderType folderType, FolderMessage folderMessage, List<String> errorInfos) {

}

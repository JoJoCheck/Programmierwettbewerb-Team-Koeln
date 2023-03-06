package gui.filehandler;

public enum FolderMessage {
    NULL_FOLDER,
    NOT_A_FOLDER,
    NO_EXISTING_FOLDER,
    NO_FILES_IN_FOLDER,
    NOT_IN_PROJECT_FOLDER,
    ERRORS_IN_FILE,
    VALID_FOLDER,
    DIFFERENT_FILE_NUMBERS;

    public static Object getMessage(FolderMessage msg, FolderType folderType) {
        return switch (msg) {
            case NULL_FOLDER -> "The " + folderType.toString().toLowerCase() + " folder is null!";
            case NOT_A_FOLDER -> "This is not a folder!";
            case NO_EXISTING_FOLDER -> "The folder does not exist!";
            case NO_FILES_IN_FOLDER -> "There are no files in the " + folderType.toString().toLowerCase() + " folder!";
            case NOT_IN_PROJECT_FOLDER -> "The " + folderType.toString().toLowerCase() + " folder is not in the project!";
            case ERRORS_IN_FILE -> folderType != null ?
                    "The " + folderType.toString().toLowerCase() + " folder contains files with errors!" :
                    "The folders contains files with errors!";
            case VALID_FOLDER -> "The " + folderType.toString().toLowerCase() + " folder is valid!";
            case DIFFERENT_FILE_NUMBERS -> "The input and output folders have different file numbers!";
        };
    }
}
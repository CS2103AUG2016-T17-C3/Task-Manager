package seedu.task.logic.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import javafx.application.Platform;
import seedu.task.MainApp;
import seedu.task.commons.core.Config;
import seedu.task.commons.exceptions.DataConversionException;
import seedu.task.commons.util.ConfigUtil;
import seedu.task.commons.util.FileUtil;
import seedu.task.commons.util.XmlUtil;
import seedu.task.model.ReadOnlyTaskManager;
import seedu.task.storage.XmlSerializableTaskManager;
import seedu.task.commons.core.LogsCenter;

//import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Saves task manager data at specified directory.
 */
public class DirectoryCommand extends Command {
    
    private static final Logger logger = LogsCenter.getLogger(ConfigUtil.class);

    public static final String COMMAND_WORD = "directory";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Load TaskManager with data in given directory. \n"
            + "Parameters: directory/filename OR filename\n"
            + "Example: " + COMMAND_WORD
            + " c:/Users/user/Desktop/TaskManagerBackup1 OR TaskManagerBackup2";

    public static final String MESSAGE_NEW_DIRECTORY_SUCCESS = "New data: %1$s";
    
    //This constant string variable is file extension of the storage file.
    private final String FILE_EXTENSION = ".xml";
    
    //This is the path of the storage file.
    private String _newFilePath;
    
    public DirectoryCommand(String newFilePath) {
        appendExtension(newFilePath);
        boolean check = new File(_newFilePath).exists();
        if (check) {
            Config config = new Config();
            File configFile = new File("config.json");
            try {
                config = FileUtil.deserializeObjectFromJsonFile(configFile, Config.class);
            } catch (IOException e) {
                logger.warning("Error reading from config file " + "config.json" + ": " + e);
                try {
                    throw new DataConversionException(e);
                } catch (DataConversionException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            
            config.setTaskManagerFilePath(_newFilePath);
            
            try {
                ConfigUtil.saveConfig(config, "config.json");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
               
        }
    }
    
    public void appendExtension(String filepath) {
        if (filepath != null) {
            _newFilePath = filepath + FILE_EXTENSION;
        }
    }

    @Override
    public CommandResult execute(boolean isUndo) {
        assert model != null;
        try {
            File newFile = new File(_newFilePath);
            ReadOnlyTaskManager newData;
            newData = XmlUtil.getDataFromFile(newFile, XmlSerializableTaskManager.class);
            model.resetData(newData);
        } catch (FileNotFoundException | JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //logger.info(String.format(MESSAGE_NEW_DIRECTORY_SUCCESS, _newFilePath));
        logger.info("============================ [ Please restart Task Manager ] =============================");
        Platform.exit();
        return new CommandResult(String.format(MESSAGE_NEW_DIRECTORY_SUCCESS, _newFilePath));
    }

    @Override
    public CommandResult execute(int index) {
        // TODO Auto-generated method stub
        return null;
    }

}
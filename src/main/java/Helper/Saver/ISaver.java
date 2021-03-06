package Helper.Saver;

import Helper.FileExceptions.MyFileException;

import java.util.List;

/**
 * Created by andrei on 2017-01-05.
 */
public interface ISaver<E> {
    void save(List<E> list, String fileName) throws MyFileException;
}

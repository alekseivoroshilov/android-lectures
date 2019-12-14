package name.ank.lab4;


import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStreamReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import name.ank.lab4.BibConfig;
import name.ank.lab4.BibDatabase;
import name.ank.lab4.BibEntry;
import name.ank.lab4.Keys;
import name.ank.lab4.Types;

public class BibDatabaseTest {

  private BibDatabase database;

  @Before
  public void setup() throws IOException {
    try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("/references.bib"))) {
      database = new BibDatabase(reader);
    }
  }

  @Test
  public void getFirstEntry() {
    BibEntry first = database.getEntry(0);
    Assert.assertEquals(Types.ARTICLE, first.getType());
    Assert.assertEquals("The semantic web", first.getField(Keys.TITLE));
    Assert.assertNull("Field 'chapter' does not exist", first.getField(Keys.CHAPTER));
  }

  @Test
  public void normalModeDoesNotThrowException() {
    BibConfig cfg = database.getCfg();
    cfg.strict = false;

    BibEntry first = database.getEntry(0);
    for (int i = 0; i < cfg.maxValid + 1; i++) {
      BibEntry unused = database.getEntry(0);
      Assert.assertNotNull("Should not throw any exception @" + i, first.getType());
    }
  }

  @Test(expected = IllegalStateException.class)
  public void strictModeThrowsException() throws IllegalStateException {
    BibConfig cfg = database.getCfg();
    cfg.strict = true;

    BibEntry first = database.getEntry(0);
    for (int i = 0; i < cfg.maxValid; i++) {
      BibEntry unused = database.getEntry(0);
    }
    first.getType();//IllegalStateException потому что после извлечения maxValid + 1 записи первая
    //стала невалидной
  }

  @Test
  public void shuffleFlag() throws IOException{

    BibConfig cfg = database.getCfg();  //Создадим объект BigConfig
    cfg.shuffle = true; //Включим в нём флаг shuffle

    boolean check=false;
    BibEntry lastEntry = null;

    for(int i=0; i<5;i++){
      try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("/shuffleReferences.bib"))) {
        database = new BibDatabase(reader);
      }
      if(lastEntry != null && database.getEntry(0).getType()!=lastEntry.getType()) check=true;
      //если тип предыдущего блока отличается от текущего (article != book)
      lastEntry = database.getEntry(0);
    }
    Assert.assertTrue(check);
  }
}

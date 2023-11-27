import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import koodit.Asiakas;
import koodit.IHinnoittelija;
import koodit.Tilaus;
import koodit.TilaustenKäsittely;
import koodit.Tuote;

public class TilaustenKäsittelyMockitoTest {

    @Mock
    private IHinnoittelija hinnoittelijaMock;

    @InjectMocks
    private TilaustenKäsittely käsittelijä;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testaaKunHintaAlleSata() {
        // Arrange
        float alkuSaldo = 100.0f;
        float listaHinta = 95.0f;
        float alennus = 10.0f;
        float loppuSaldo = alkuSaldo - (listaHinta * (1 - alennus / 100));
        Asiakas asiakas = new Asiakas(alkuSaldo);
        Tuote tuote = new Tuote("TDD in Action", listaHinta);
        
        when(hinnoittelijaMock.getAlennusProsentti(asiakas, tuote)).thenReturn(alennus);
        
        // Act
        käsittelijä.käsittele(new Tilaus(asiakas, tuote));
        
        // Assert
        assertEquals(loppuSaldo, asiakas.getSaldo(), 0.001);
        verify(hinnoittelijaMock, times(2)).getAlennusProsentti(asiakas, tuote);
        verify(hinnoittelijaMock).aloita();
        verify(hinnoittelijaMock, never()).setAlennusProsentti(any(Asiakas.class), anyFloat());
        verify(hinnoittelijaMock).lopeta();
    }

    @Test
    public void testaaKunHintaYliSata() {
        // Arrange
        float alkuSaldo = 100.0f;
        float listaHinta = 150.0f;
        float alennus = 10.0f;
        float loppuSaldo = alkuSaldo - (listaHinta * (1 - (alennus + 5) / 100));
        Asiakas asiakas = new Asiakas(alkuSaldo);
        Tuote tuote = new Tuote("Refactoring to Patterns", listaHinta);
        
        hinnoittelijaMock.setAlennusProsentti(asiakas, alennus + 5);
        when(hinnoittelijaMock.getAlennusProsentti(asiakas, tuote)).thenReturn(alennus + 5);
        
        // Act
        käsittelijä.käsittele(new Tilaus(asiakas, tuote));
        
        // Assert
        assertEquals(loppuSaldo, asiakas.getSaldo(), 0.001);
        verify(hinnoittelijaMock, times(2)).getAlennusProsentti(asiakas, tuote);
        verify(hinnoittelijaMock).aloita();
        verify(hinnoittelijaMock).setAlennusProsentti(asiakas, alennus + 5);
        verify(hinnoittelijaMock).lopeta();
    }
}

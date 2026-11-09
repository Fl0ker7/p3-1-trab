package menu;

import java.awt.Point;
import java.time.LocalDateTime;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import pds.dominio.TipoServico;
import pds.equipamentos.Equipamento;
import pds.equipamentos.PortaEntrada;
import pds.equipamentos.PortaPiscina;
import pds.ginasio.Ginasio;
import pds.ginasio.GinasioExemplo;
import pds.util.RelogioSimulado;

public class Arranque {

	public static void main(String[] args) {
		// Criar o relógio simulado e "acertá-lo" para a hora atual
		RelogioSimulado relogio = RelogioSimulado.getRelogioSimulado();
		relogio.setTempoAtual(LocalDateTime.now());
		Ginasio ginasio = GinasioExemplo.criar();

		SwingUtilities.invokeLater(() -> {
			JanelaGinasio main = new JanelaGinasio(ginasio);
			for (Equipamento equipamento : ginasio.getEquipamentos()) {
				main.addEquipamento(equipamento.getId(), getIcons(getIconName(equipamento)));
			}

			main.setVisible(true);
			RelogioDialog relog = new RelogioDialog(main, new Point(200, 20));
			relog.setVisible(true);

		});
	}

	/**
	 * Lê os ficheiros com os icons para um dado equipamento
	 * 
	 * @param nome nome da imagem representativa do equipamento
	 * @return os icones com a imagem livre e ocupado
	 */
	private static ImageIcon[] getIcons(String nome) {
		String pasta = "art/";
		ImageIcon icons[] = { new ImageIcon(pasta + nome + "_livre.png"),
				new ImageIcon(pasta + nome + "_ocupado.png") };
		return icons;
	}

	private static String getIconName(Equipamento equipamento) {
		if (equipamento instanceof PortaPiscina) {
			return "porta_piscina";
		}
		if (equipamento instanceof PortaEntrada || equipamento.getServico().getTipo() == TipoServico.SAIDA) {
			return "porta";
		}
		switch (equipamento.getServico().getTipo()) {
			case AULA:
				return "aula";
			case PISCINA:
				return "porta_piscina";
			case DUCHE:
				return "duche";
			case BEBIDA:
				return "bebida";
			case PERSONAL_TRAINER:
				return "pt";
			case NUTRICAO:
				return "nutricionista";
			case AVALIACAO_FISICA:
				return "fisica";
			case MASSAGEM:
				return "poltrona";
			case ENTRADA:
			case SAIDA:
			default:
				return "porta";
		}
	}
}

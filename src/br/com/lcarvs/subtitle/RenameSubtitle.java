package br.com.lcarvs.subtitle;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RenameSubtitle {
	private static final Pattern PATTERN = Pattern.compile("s[\\d]{1,2}e[\\d]{1,2}", Pattern.CASE_INSENSITIVE);
	private static final String SRT = ".srt";
	private static final String MKV = ".mkv";

	public static void main(String[] args) {
		if (args.length == 0) {
			throw new MissingResourceException("Pasta raiz não definida", "RenameSubtitle", "diretorio");
		}

		File rootFolder = new File(args[0]);
		renomearLegenda(rootFolder);
	}

	public static String encontraCodigoEpisodio(String texto) {
		Matcher matcher = PATTERN.matcher(texto);
		if (matcher.find()) {
			return matcher.group(0);
		}

		return null;
	}

	public static void renomearLegenda(File file) {
		File[] diretorios = file.listFiles(File::isDirectory);
		File[] arquivos = file.listFiles(File::isFile);

		for (File diretorio : diretorios) {
			renomearLegenda(diretorio);
		}

		if (arquivos.length > 0) {
			File[] episodios = file.listFiles((dir, name) -> name.toLowerCase().endsWith(MKV));
			List<File> legendas = Arrays.asList(file.listFiles((dir, name) -> name.toLowerCase().endsWith(SRT)));

			for (File episodio : episodios) {
				String codigo = encontraCodigoEpisodio(episodio.getName());
				Optional<File> optionalFile = legendas.stream().filter((x) -> x.getName().contains(codigo)).findFirst();

				if (optionalFile.isPresent()) {
					File legenda = optionalFile.get();
					legenda.renameTo(new File(episodio.getPath().replace(MKV, SRT)));
				}
			}
		}

	}
}

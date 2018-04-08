package ar.com.meli.melitestmutant;

public class MutantUtils {

	private static int height = 0;
	private static int width = 0;

	private static int sameLetter = 0;

	private static int coincidences = 0;

	private static int posX = 0;
	private static int posY = 0;

	public static Boolean isMutant(String[] dna) {
		coincidences = 0;
		sameLetter = 0;
		height = dna.length;
		width = dna[0].length();

		Character[][] matrizDNA = new Character[height][width];

		for (int i = 0; i < height; i++) {
			matrizDNA[i] = dna[i].chars().mapToObj(c -> (char) c).toArray(Character[]::new);
		}

		return checkMutant(matrizDNA);
	}

	private static Boolean checkMutant(Character[][] matrizDNA) {
		int i = 0;
		int y = 0;
		while (i < height && coincidences < 2) {
			posY = 0;
			findToRigth(matrizDNA[i]);
			i++;
		}

		i = 0;
		while (i < width && coincidences < 2) {
			posY = i;
			posX = 0;
			findToDownSide(matrizDNA);
			i++;
		}

		i = 0;
		y = 0;
		while (i < height && coincidences < 2) {
			posX = i;
			posY = 0;
			while (y < width && coincidences < 2) {
				posY = y;
				findDiagonalDown(matrizDNA);
				y++;
			}
			i++;
		}

		i = height - 1;
		y = 0;
		while (i > 1 && coincidences < 2) {
			posX = i;
			posY = 0;
			while (y < width && coincidences < 2) {
				posX = i;
				posY = y;
				findDiagonalUp(matrizDNA);
				y++;
			}
			i--;
			y = 0;
		}

		return coincidences > 1;
	}

	private static void findToRigth(Character[] row) {
		if (coincidences < 2) {
			int neededLetters = 4 - sameLetter;
			int freeSpaces = width - (posY + 1);
			if (freeSpaces >= neededLetters) {
				if (row[posY].equals(row[posY + 1])) {
					sameLetter = sameLetter + 1;
					if (sameLetter == 4) {
						coincidences = coincidences + 1;
						if (freeSpaces > 4) {
							sameLetter = 1;
							posY = posY + 1;
						} else {
							return;
						}
					}
				} else {
					sameLetter = 1;
				}
				posY = posY + 1;
				findToRigth(row);
			} else {
				return;
			}
		} else {
			return;
		}
	}

	private static void findToDownSide(Character[][] matrizDNA) {
		if (coincidences < 2) {
			int neededLetters = 4 - sameLetter;
			int freeSpaces = height - (posX + 1);
			if (freeSpaces >= neededLetters) {
				if (matrizDNA[posX][posY].equals(matrizDNA[posX + 1][posY])) {
					sameLetter = sameLetter + 1;
					if (sameLetter == 4) {
						coincidences = coincidences + 1;
						if (freeSpaces > 4) {
							sameLetter = 1;
							posX = posX + 1;
						} else {
							return;
						}
					}
				} else {
					sameLetter = 1;
				}
				posX = posX + 1;
				findToDownSide(matrizDNA);
			} else {
				return;
			}
		} else {
			return;
		}
	}

	private static void findDiagonalDown(Character[][] matrizDNA) {
		if (coincidences < 2) {
			int neededLetters = 4 - sameLetter;
			int freeSpacesHorizontal = width - (posY + 1);
			int freeSpacesVertical = height - (posX + 1);

			if (freeSpacesHorizontal >= neededLetters && freeSpacesVertical >= neededLetters) {
				if (matrizDNA[posX][posY].equals(matrizDNA[posX + 1][posY + 1])) {
					sameLetter = sameLetter + 1;
					if (sameLetter == 4) {
						coincidences = coincidences + 1;
						if (freeSpacesHorizontal > 4 && freeSpacesVertical > 4) {
							sameLetter = 1;
							posX = posX + 1;
							posY = posY + 1;
						} else {
							return;
						}
					}
				} else {
					sameLetter = 1;
				}
				posX = posX + 1;
				posY = posY + 1;
				findDiagonalDown(matrizDNA);
			} else {
				return;
			}
		} else {
			return;
		}
	}

	private static void findDiagonalUp(Character[][] matrizDNA) {
		if (coincidences < 2) {
			int neededLetters = 4 - sameLetter;
			int freeSpacesHorizontal = width - (posY + 1);
			int freeSpacesVertical = posX;
			if (freeSpacesHorizontal >= neededLetters && freeSpacesVertical >= neededLetters) {
				if (matrizDNA[posX][posY].equals(matrizDNA[posX - 1][posY + 1])) {
					sameLetter = sameLetter + 1;
					if (sameLetter == 4) {
						coincidences = coincidences + 1;
						if (freeSpacesHorizontal > 4 && freeSpacesVertical > 4) {
							sameLetter = 1;
							posX = posX - 1;
							posY = posY + 1;
						} else {
							return;
						}
					}
				} else {
					sameLetter = 1;
				}
				posX = posX - 1;
				posY = posY + 1;
				findDiagonalUp(matrizDNA);
			} else {
				return;
			}
		} else {
			return;
		}
	}

}

import java.awt.Color;


public class SeamCarver {
    private int height;
    private int width;

    private byte[] red;
    private byte[] green;
    private byte[] blue;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new java.lang.NullPointerException();

        height = picture.height();
        width = picture.width();

        copyPicture(picture);
    }

    // energy of pixel at column x and row y
    public double energy(int j, int i) {
        if (i < 0 || i > height() - 1 || j < 0 || j > width() - 1)
            throw new java.lang.IndexOutOfBoundsException();

        if (i == 0 || i == height() - 1 || j == 0 || j == width() - 1)
            return 195075;

        int left = getIndex(i, j - 1);
        int right = getIndex(i, j + 1);

        int up = getIndex(i - 1, j);
        int down = getIndex(i +  1, j);

        return gradient(right, left) + gradient(down, up);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        transpose();
        int [] seam = findVerticalSeam();
        transpose();

        return seam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int[] distTo = new int[width()];
        int[] min = new int[width()];
        int[][] edgeTo = new int[height()][width()];

        for (int j = 0; j < width(); ++j)
            distTo[j] = (int) energy(j, 0);

        for (int i = 1; i < height(); ++i) {
            for (int j = 0; j < width(); ++j) {
                min[j] = Integer.MAX_VALUE;

                for (int col = j - 1; col <= j + 1; ++col) {

                    if (col < 0 || col > width() - 1)
                        continue;

                    int distance = distTo[col] + (int) energy(j, i);

                    if (distance < min[j]) {
                        min[j] = distance;
                        edgeTo[i][j] = col;
                    }
                }
            }

            System.arraycopy(min, 0, distTo, 0, width());
        }

        int index = argmin(distTo);
        int[] seams = new int[height()];

        for (int i = height() - 1; i >= 0; --i) {
            seams[i] = index;
            index = edgeTo[i][index];
        }

        return seams;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // current picture
    public Picture picture() {
        Picture picture = new Picture(width(), height());

        for (int i = 0; i < height(); ++i)
            for (int j = 0; j < width(); ++j) {
                Color color = color(i, j);
                picture.set(j, i, color);
            }

        return picture;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        transpose();
        removeVerticalSeam(seam);
        transpose();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null)
            throw new java.lang.NullPointerException();

        if (width() < 2 || seam.length != height())
            throw new java.lang.IllegalArgumentException();

        --width;

        red = shiftArray(red, seam);
        green = shiftArray(green, seam);
        blue = shiftArray(blue, seam);
    }

    // width of current picture
    public int width() {
        return width;
    }

    private int argmin(int[] array) {
        int index = -1;
        int min = Integer.MAX_VALUE;

        for (int i = 0; i < array.length; ++i)
            if (array[i] < min) {
                index = i;
                min = array[i];
            }

        return index;
    }

    private int byteToInt(byte b) {
        return ((int) b) & 0xff;
    }

    private Color color(int i, int j) {
        int index = getIndex(i, j);
        return new Color(byteToInt(red[index]),
                byteToInt(green[index]),
                byteToInt(blue[index]));
    }

    private void copyPicture(Picture picture) {

        red = new byte[height() * width()];
        green = new byte[height() * width()];
        blue = new byte[height() * width()];

        int index = 0;

        for (int i = 0; i < height(); ++i)
            for (int j = 0; j < width(); ++j) {
                Color color = picture.get(j, i);

                red[index] = (byte) (color.getRed());
                green[index] = (byte) (color.getGreen());
                blue[index] = (byte) (color.getBlue());

                ++index;
            }
    }

    private int getIndex(int i, int j) {
        return i * width() + j;
    }

    private int gradient(int a, int b) {
        int gradient = 0;

        gradient += square(byteToInt(red[b]) - byteToInt(red[a]));
        gradient += square(byteToInt(green[b]) - byteToInt(green[a]));
        gradient += square(byteToInt(blue[b]) - byteToInt(blue[a]));

        return gradient;
    }

    private byte[] shiftArray(byte[] array, int[] seam) {  // TODO: use System.arraycopy
        byte[] tmp = new byte[height() * width()];

        for (int i = 0; i < height(); ++i) {
            int offset = 0;

            for (int j = 0; j < width(); ++j) {
                if (seam[i] == j)
                    ++offset;

                tmp[getIndex(i, j)] = array[i * (width() + 1) + j + offset];
            }
        }

        return tmp;
    }

    private int square(int x) {
        return x * x;
    }

    private void transpose() {
        red = transposeArray(red);
        green = transposeArray(green);
        blue = transposeArray(blue);

        int tmp = height;
        height = width;
        width = tmp;
    }

    private byte[] transposeArray(byte[] array) {  // TODO: transpose in-place

        byte[] tmp = new byte[height() * width()];

        for (int i = 0; i < height(); ++i)
            for (int j = 0; j < width(); ++j) {
                tmp[j * height() + i] = array[getIndex(i, j)];
            }

        return tmp;
    }
}

import java.util.Objects;

public class Csv {
    private String palavra;
    private Long nanoTime;
    private Integer threds;
    private Double speedUp;

    public String getPalavra() {
        return palavra;
    }

    public void setPalavra(String palavra) {
        this.palavra = palavra;
    }

    public Long getNanoTime() {
        return nanoTime;
    }

    public void setNanoTime(Long nanoTime) {
        this.nanoTime = nanoTime;
    }

    public Integer getThreds() {
        return threds;
    }

    public void setThreds(Integer threds) {
        this.threds = threds;
    }

    public Double getSpeedUp() {
        return speedUp;
    }

    public void setSpeedUp(Double speedUp) {
        this.speedUp = speedUp;
    }

    public Csv() {
    }

    public Csv(String palavra, Long nanoTime, Integer threds, Double speedUp) {
        this.palavra = palavra;
        this.nanoTime = nanoTime;
        this.threds = threds;
        this.speedUp = speedUp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Csv csv = (Csv) o;
        return Objects.equals(palavra, csv.palavra) &&
                Objects.equals(nanoTime, csv.nanoTime) &&
                Objects.equals(threds, csv.threds) &&
                Objects.equals(speedUp, csv.speedUp);
    }

    @Override
    public int hashCode() {

        return Objects.hash(palavra, nanoTime, threds, speedUp);
    }
}

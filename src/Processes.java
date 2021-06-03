public class Processes {
    private int index; // tên process. Ví dụ P + index -> P1, P2,...
    private int arrivalTime; // thời gian xuất hiện
    private int burstTime; // CPU burst time

    public Processes(int index, int arrivalTime, int burstTime) {
        this.index = index;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
    }
}

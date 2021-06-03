import java.util.ArrayList;
import java.util.Scanner;

public class RunMain {
    private static Scanner scanner = new Scanner(System.in);
    private static ArrayList<Processes> listProcesses = new ArrayList<>(); // Danh sách các tiến trình
    private static int timeQuantum; // time quantum
    private static ArrayList<Integer> completeTime = new ArrayList<>(); // mảng thời gian kết thúc tiến trình
    private static ArrayList<Integer> waitQueueIndex; //  mảng hàng đợi (thứ tự xử lý của các tiến trình)
    private static ArrayList<Integer> burstTimeCopy = new ArrayList<>(); // mảng chứa CPU burst time phụ
    private static ArrayList<Integer> processTime; // mảng thời gian xử lý
    private static ArrayList<Integer> waitTime; // mảng thời gian đợi
    private static int startTime; // thời gian bắt đầu
    private static boolean checked = true; // kiểm tra
    private static int processingTime; // thời gian đang xử lý hiện tại
    private static int indexWait; // index mảng hàng đợi
    private static int nextProcessId; // index tiến trình tiếp theo (dựa vào mảng hàng đợi)
    private static float sumProcessTime = 0, sumWaitTime = 0; // tổng thời gian xử lý và tổng thời gian đợi
    private static float processTimeAverage; // thời gian xử lý trung bình
    private static float waitTimeAverage; // thời gian chờ đợi trung bình

    // test: 5 0 5 1 3 3 6 5 1 6 4 3
    // test: 6 0 4 2 5 4 6 6 3 7 2 9 4 3
    public static void main(String[] args) {
        System.out.print("Enter amount processes: ");
        int n = scanner.nextInt();
        inputProcesses(listProcesses, burstTimeCopy, n); // nhập vào danh sách các tiến trình và time quantum
        System.out.println("\n--------------------------------------------------------------------------------------------------------------");
        System.out.println("\tToàn Bộ Tiến Trình Vừa Nhập:");
        showProcesses(listProcesses, listProcesses.size()); // hiển thị toàn bộ tiến trình
        sortProcessesByArrivalTime(listProcesses); // sắp xếp tiến trình theo thời gian xuất hiện (tăng dần)
        System.out.println("--------------------------------------------------------------------------------------------------------------");
        System.out.println("\tToàn Bộ Tiến Trình Sau Khi Sắp Xếp Theo Thời Gian Xuất Hiện: (tăng dần)");
        showProcesses(listProcesses, listProcesses.size()); // hiển thị lại toàn bộ tiến trình sau khi sắp xếp

        // hiển thị kết quả
        System.out.println("--------------------------------------------------------------------------------------------------------------");
        System.out.println("GANTT SCHEMATiIC DIAGRAM");
        System.out.print("[" + listProcesses.get(0).getArrivalTime() + "]"); // hiển thị [0] (thời điểm bắt đầu).
        do {
            if (checked) {
                startTime = listProcesses.get(0).getArrivalTime(); // gán thời gian bắt đầu = phần tử đầu tiên trong mục Thời gian xuất hiện của danh sách
                if (burstTimeCopy.get(0) <= timeQuantum) { // nếu CPUb <= Time quantum
                    processingTime = startTime + burstTimeCopy.get(0); // gán lại thời gian đang xử lý = thời gian bắt đầu + CPUb của tiến trình đang xử lý
                    firstFindStack(nextProcessId, processingTime); // tìm index tiếp theo để tiến hành xử lý
                } else {
                    burstTimeCopy.set(0, burstTimeCopy.get(0) - timeQuantum); // nếu CPUb > time quantum thì Burst time của
                    // tiến trình hiện tại sẽ được gán lại = CPUb - time quantum
                    processingTime = startTime + timeQuantum; // gán lại thời gian xử lý bằng = thời gian bắt đầu + time quantum
                    firstFindStack(nextProcessId, processingTime); // tìm index tiếp theo để tiến hành xử lý
                    addQueue(nextProcessId); // trong khi tiến trình đang xử lý thì tiến trình khác xuất hiện, đưa tiến trình xuất hiện đó vào hàng đợi
                }
            } else {
                nextProcessId = waitQueueIndex.get(0) - 1; // lấy index tiếp theo để xử lý
                startTime = processingTime; // gán thời gian bắt đầu = thời gian đang xử lý
                for (int i = 0; i < indexWait && indexWait != 1; i++) {
                    waitQueueIndex.set(i, waitQueueIndex.get(i + 1)); // Xoá phần tử đầu của mảng waitQueueIndex. Do ở trên đã lấy phần tử
                    // đầu của mảng waitQueueIndex nên phải xoá phần tử đầu tiên bằng cách dịch mảng sang trái
                }
                indexWait--;
                if (burstTimeCopy.get(nextProcessId) <= timeQuantum) { // nếu CPUb <= time quantum
                    processingTime = startTime + burstTimeCopy.get(nextProcessId); // thời gian đang xử lý = thời gian bắt đầu + CPUb của tiến trình đang xử lý
                    burstTimeCopy.set(nextProcessId, 0); // gán lại CPUb của tiến trình đang xử lý = 0
                    secondFindStack(nextProcessId, processingTime); // tìm index tiến trình tiếp theo
                } else {
                    burstTimeCopy.set(nextProcessId, burstTimeCopy.get(nextProcessId) - timeQuantum); // nếu CPUb > time quantum thì Burst time của
                    // tiến trình hiện tại sẽ được gán lại = CPUb - time quantum
                    processingTime = startTime + timeQuantum; // thời gian đang xử lý = thời gian bắt đầu + time quantum
                    secondFindStack(nextProcessId, processingTime); // tìm index tiếp theo để tiến hành xử lý
                    addQueue(nextProcessId); // trong khi tiến trình đang xử lý thì tiến trình khác xuất hiện, đưa tiến trình xuất hiện đó vào hàng đợi
                }
            }

            // nếu CPU burst time của một tiến trình = 0 thì thời gian kết thúc tiến trình = thời gian đang xử lý.
            if (burstTimeCopy.get(nextProcessId) == 0) {
                completeTime.set(nextProcessId, processingTime);
            }

            checked = false;

            // hiển thị sơ đồ Gantt: - P + index (tên tiến trình) - [ thời gian xử lý hiện tại ].
            System.out.print(" - P" + (nextProcessId) + " - [" + processingTime + "]");
        } while (indexWait != 0);
        // hàm hiển thị kết quả xử lý
        showAllResult(listProcesses, completeTime, processTime, waitTime);
    }


    // hiển thị kết quả
    private static void showAllResult(ArrayList<Processes> listProcesses, ArrayList<Integer> completeTime, ArrayList<Integer> processTime, ArrayList<Integer> waitTime) {
        for (int i = 0; i < listProcesses.size(); i++) {
            int processTimeValue = completeTime.get(i) - listProcesses.get(i).getArrivalTime(); // thời gian xử lý = thời gian kết thúc - thời gian xuất hiện
            int waitTimeValue = processTimeValue - listProcesses.get(i).getBurstTime(); // thời gian đợi = thời gian xử lý - CPU burst time.
            processTime.add(processTimeValue);
            waitTime.add(waitTimeValue);
        }

        for (Integer item : processTime) {
            sumProcessTime += item; // duyệt mảng processTime và cộng tổng các phẩn tử lại.
        }
        for (Integer item : waitTime) {
            sumWaitTime += item; // duyệt mảng waitTime và cộng tổng các phẩn tử lại.
        }
        processTimeAverage = sumProcessTime / listProcesses.size(); // thời gian xử lý trong bình = tổng thời gian xử lý / tổng số tiến trình.
        waitTimeAverage = sumWaitTime / listProcesses.size(); // thời gian đợi trong bình = tổng thời gian đợi / tổng số tiến trình.
        System.out.println();
        listProcesses.size();
        System.out.println("--------------------------------------------------------------------------------------------------------------");
        System.out.println("FINAL RESULT: ");
        System.out.printf("\t%5s%10s%15s%15s%15s%15s%15s", "STT", "Process", "Arrival Time", "Burst Time", "Complete Time", "Process Time", "Wait Time" + "\n");
        for (int i = 0; i < listProcesses.size(); i++) {
            System.out.printf("\t%5s%10s%15s%15s%15s%15s%15s",
                    (i + 1),  // STT
                    ("P" + listProcesses.get(i).getIndex()), // tên tiến trình P0 P1...
                    listProcesses.get(i).getArrivalTime(), // thời gian xuất hiện tiến trình.
                    listProcesses.get(i).getBurstTime(), // CPU burst time.
                    completeTime.get(i), // thời gian kết thúc tiến trình.
                    processTime.get(i), // thời gian xử lý tiến trình.
                    waitTime.get(i) + "\n"); // thời gian chờ đợi của tiến trình.
        }
        System.out.println("--------------------------------------------------------------------------------------------------------------");
        System.out.println("TOTAL");
        System.out.println("Process time: " + sumProcessTime); // hiển thị tổng thời gian xử lý
        System.out.println("Wait time: " + sumWaitTime); // hiển thị tổng thời gian đợi
        System.out.println("--------------------------------------------------------------------------------------------------------------");
        System.out.println("AVERAGE: ");
        System.out.println("Process Time Average: " + processTimeAverage); // hiển thị thời gian xử lý trung bình.
        System.out.println("Wait Time Average: " + waitTimeAverage); // hiển thị thời gian đợi trung bình.
        System.out.println("--------------------------------------------------------------------------------------------------------------");

    }


    // thêm tiến trình sau vào hàng đợi
    private static void addQueue(int nextProcessId) {
        waitQueueIndex.set(indexWait, nextProcessId + 1);
        indexWait++;
    }


    // tìm vị trí cho tiến trình tiếp theo
    private static void secondFindStack(int nextProcessId, int timeProcessing) {
        for (int i = nextProcessId + 1; i < listProcesses.size(); i++) {
            boolean check = true;
            for (int j = 0; j < indexWait; j++) {
                if (waitQueueIndex.get(j) == i + 1) {
                    check = false;
                }
            }
            if (listProcesses.get(i).getArrivalTime() <= timeProcessing && check && burstTimeCopy.get(i) != 0) {
                waitQueueIndex.set(indexWait, i + 1);
                indexWait++;
            }
        }
    }


    // tìm vị trí cho tiến trình tiếp theo
    private static void firstFindStack(int nextProcessId, int timeProcess) {
        for (int i = nextProcessId + 1; i < listProcesses.size(); i++) {
            if (listProcesses.get(i).getArrivalTime() <= timeProcess) {
                waitQueueIndex.set(indexWait, i + 1);
                indexWait++;
            }
        }
    }


    // sắp xếp lại danh sách tiến trình
    private static void sortProcessesByArrivalTime(ArrayList<Processes> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(i).getArrivalTime() > list.get(j).getArrivalTime()) {
                    Processes temp = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, temp);
                }
            }
        }
    }


    // hiển thị toàn bộ danh sách tiến trình
    private static void showProcesses(ArrayList<Processes> list, int n) {
        System.out.printf("\t%5s%10s%15s%15s", "STT", "Process", "Arrival Time", "Burst Time\n");
        for (int i = 0; i < n; i++) {
            System.out.printf("\t%5s%10s%15s%15s", i + 1, "P" + list.get(i).getIndex(), list.get(i).getArrivalTime(), list.get(i).getBurstTime() + "\n");
        }
        System.out.println("\tTime quantum: " + timeQuantum);
    }


    // nhập tiến trình
    private static void inputProcesses(ArrayList<Processes> list, ArrayList<Integer> burstTimeCopy, int n) {
        System.out.println("--------------------------------------------------------------------------------------------------------------");
        System.out.println("\tENTER PROCESSES: \n");
        for (int i = 0; i < n; i++) {
            int index = i;
            System.out.println("\t\tProcess P" + i + ": ");
            System.out.print("\t\tArrival Time: ");
            int arrivalTime = scanner.nextInt();
            System.out.print("\t\tBurst Time: ");
            int burstTime = scanner.nextInt();
            System.out.println();
            list.add(new Processes(index, arrivalTime, burstTime));
            burstTimeCopy.add(burstTime);
//            burstTimeCopy.size();
        }
        System.out.print("\t\tEnter time quantum: ");
        timeQuantum = scanner.nextInt();

        // khởi tạo mảng waitQueueIndex, processTime, waitTime
        waitQueueIndex = new ArrayList<>();
        processTime = new ArrayList<>();
        waitTime = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            waitQueueIndex.add(0);
//            processTime.add(0);
//            waitTime.add(0);
            completeTime.add(0);
        }

    }
}

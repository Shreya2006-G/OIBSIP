
public class TrainConnection {

    private final Train firstTrain;
    private final Train secondTrain;

    public TrainConnection(Train firstTrain, Train secondTrain) {
        if (firstTrain == null || secondTrain == null) {
            throw new IllegalArgumentException(
                    "Both trains are required for a connection."
            );
        }

        if (firstTrain.getTrainNumber() == secondTrain.getTrainNumber()) {
            throw new IllegalArgumentException(
                    "A connecting journey must use two different trains."
            );
        }

        this.firstTrain = firstTrain;
        this.secondTrain = secondTrain;
    }

    public Train getFirstTrain() {
        return firstTrain;
    }

    public Train getSecondTrain() {
        return secondTrain;
    }

    // The station where passengers change trains.
    public String getConnectingStation() {
        return firstTrain.getDestinationStation();
    }

    // Origin of the complete journey.
    public String getSourceStation() {
        return firstTrain.getSourceStation();
    }

    // Final destination of the complete journey.
    public String getDestinationStation() {
        return secondTrain.getDestinationStation();
    }

    // Helpful summary for displaying the journey.
    public String getJourneySummary() {
        return getSourceStation()
                + " → " + getConnectingStation()
                + " → " + getDestinationStation();
    }
}

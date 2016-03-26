package koh.glicko;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;

/**
 * Created by Melancholia on 3/18/16.
 */
public class Glicko2Player {

    private double rating;
    private double ratingDeviation; //Rating deviation
    private double sigma;
    private double mu;
    private double phi;
    private double tau;
    private static final double pi2 = 9.8696044;
    private final ArrayList<double[]> M = new ArrayList<>(3);

    //rating = 1500, rd = 350, volatility = 0.06, mu = null, phi = null, sigma = null, systemconstant = 0.75

    public Glicko2Player(double rating, String[] values){
        this.rating = rating;
        this.ratingDeviation = Double.parseDouble(values[0]);
        this.sigma = Double.parseDouble(values[1]);
        this.mu = Double.parseDouble(values[2]);
        this.phi = Double.parseDouble(values[3]);
        this.tau = Double.parseDouble(values[4]);
    }

    public static Glicko2Player defaultValue() {
        return new Glicko2Player(1500, 350, 0.06, null, null, null, 0.75);
    }

    public Glicko2Player(double rating, double ratingDeviation, double volatility, Double mu, Double phi, Double sigma, double systemConstant) {
        this.rating = rating;
        this.ratingDeviation = ratingDeviation;
        // volatility
        if (sigma == null) {
            this.sigma = volatility;
        } else {
            this.sigma = sigma;
        }
        // System Constant
        this.tau = systemConstant;
        // Step 2
        // Rating
        if (mu == null) {
            this.mu = (this.rating - 1500) / 173.7178;
        } else {
            this.mu = mu;
        }
        // Rating Deviation
        if (phi == null) {
            this.phi = this.ratingDeviation / 173.7178;
        } else {
            this.phi = phi;
        }
    }

    public double[] matchElement(double score) {
        return new double[]{this.mu, this.phi, score};
    }

    public void addWin(Glicko2Player otherPlayer) {
        this.M.add(otherPlayer.matchElement(1));
    }

    public void addLoss(Glicko2Player otherPlayer) {
        this.M.add(otherPlayer.matchElement(0));
    }

    public void addDraw(Glicko2Player otherPlayer) {
        this.M.add(otherPlayer.matchElement(0.5));
    }

    public void update() {
        final double[] results = this.addMatches(this.M);
        this.rating = results[0];
        if(this.rating < 1500){
            this.rating = 1500;
        }
        this.ratingDeviation = results[1];
        this.mu = results[2];
        this.phi = results[3];
        this.sigma = results[4];
        this.M.clear();
    }

    private final double[] addMatches(ArrayList<double[]> M) {
        // This is where the Glicko2 rating calculation actually happens
        // Follow along the steps using: http://www.glicko.net/glicko/glicko2.pdf
        if (M.isEmpty()) {
            final double phi_p = Math.sqrt((this.phi * this.phi) + (this.sigma * this.sigma));
            return new double[]{this.rating, 173.7178 * phi_p, this.mu, phi_p, this.sigma};
        }
        // summation parts of Step 3 & 4 & 7
        double v_sum = 0;
        double delta_sum = 0;
        double mu_p_sum = 0;
        for (double[] m : M) {
            final double E = this.E(this.mu, m[0], m[1]);
            final double g = this.g(m[1]);
            v_sum += (g * g * E * (1 - E));
            delta_sum += g * (m[2] - E);
            mu_p_sum += g * (m[2] - E);
        }
        // Step 3
        // Estimated variance
        double v = 1.0 / v_sum;
        // Step 4
        // Estimated improvment in rating
        double delta = v * delta_sum;
        // Step 5
        final double a = Math.log(this.sigma * this.sigma);
        double x_prev = a;
        double x = x_prev;
        final double tausq = this.tau * this.tau;
        final double phisq = this.phi * this.phi;
        final double deltasq = delta * delta;
        do {
            double exp_xp = Math.exp(x_prev);
            double d = this.phi * this.phi + v + exp_xp;
            double deltadsq = deltasq / (d * d);
            double h1 = -(x_prev - a) / (tausq) - (0.5 * exp_xp / d) + (0.5 * exp_xp * deltadsq);
            double h2 = (-1.0 / tausq) - ((0.5 * exp_xp) * (phisq + v) / (d * d)) + (0.5 * deltasq * exp_xp * (phisq + v - exp_xp) / (d * d * d));
            double tmp_x = x;
            x = x_prev - (h1 / h2);
            x_prev = tmp_x;
        } while (Math.abs(x - x_prev) > 0.1);
        final double sigma_p = Math.exp(x / 2);
        // Step 6
        final double phi_star = Math.sqrt(phisq + (sigma_p * sigma_p));
        // Step 7
        final double phi_p = 1.0 / (Math.sqrt((1.0 / (phi_star * phi_star)) + (1.0 / v)));
        // New mu
        final double mu_p = this.mu + phi_p * phi_p * mu_p_sum;
        return new double[]{(173.7178 * mu_p) + 1500, 173.7178 * phi_p, mu_p, phi_p, sigma_p};
        //R, rd, , mu , phi, sigma
    }


    private double g(double phi) {
        return 1.0 / (Math.sqrt(1.0 + (3.0 * phi * phi) / (pi2)));
    }

    private double E(double mu, double mu_j, double phi_j) {
        return 1.0 / (1.0 + Math.exp(-this.g(phi_j) * (mu - mu_j)));
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public int getRating(){
        return (int) this.rating;
    }


    public String serialize(){
        final StringBuilder sb = new StringBuilder();
        sb.append(ratingDeviation).append(',');
        sb.append(sigma).append(',');
        sb.append(mu).append(',');
        sb.append(phi).append(',');
        sb.append(tau);
        return sb.toString();
    }


}

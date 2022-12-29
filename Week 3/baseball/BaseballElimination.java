/* *****************************************************************************
 *  Name:        Dale Young
 *  Date:        12/28/2022
 *  Description: A program that represents a sports division and determines which
                 teams are mathematically eliminated.
 **************************************************************************** */

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaseballElimination {

    private static final double EPSILON = 0.00001;
    private int numOfTeams;
    private ArrayList<Team> teams = new ArrayList<>();
    private int[][] against;
    private int maxWin = 0;
    private String maxTeam = "";

    private HashMap<String, List<String>> certificate;

    private class Team {
        private int id;
        private String name;
        private int wins;
        private int losses;
        private int remainings;

        public Team(int i, String n, int w, int lo, int r) {
            id = i;
            name = n;
            wins = w;
            losses = lo;
            remainings = r;
        }
    }

    /** create a baseball division from given filename in specified format */
    public BaseballElimination(String filename) {
        In in = new In(filename);
        numOfTeams = in.readInt();
        against = new int[numOfTeams][numOfTeams];
        certificate = new HashMap<>();

        for (int i = 0; i < numOfTeams; i += 1) {
            String name = in.readString();
            int wins = in.readInt();
            int losses = in.readInt();
            int remainings = in.readInt();
            teams.add(new Team(i, name, wins, losses, remainings));

            for (int j = 0; j < numOfTeams; j += 1) {
                against[i][j] = in.readInt();
            }

            if (wins > maxWin) {
                maxWin = wins;
                maxTeam = name;
            }
        }

    }

    private FlowNetwork createFlowNetwork(Team team) {
        int numOfGames = numOfTeams * (numOfTeams - 1) / 2;
        int numOfVertices = numOfGames + numOfTeams + 2;
        int source = numOfVertices - 2;
        int target = numOfVertices - 1;
        int sum = team.wins + team.remainings;

        FlowNetwork fn = new FlowNetwork(numOfVertices);
        for (int i = 0; i < numOfTeams; i += 1) {
            if (i != team.id) {
                fn.addEdge(new FlowEdge(i, target, sum - teams.get(i).wins));
            }

        }
        for (int i = 0, v = numOfTeams; i < numOfTeams; i += 1) {
            if (i != team.id) {
                for (int j = i + 1; j < numOfTeams; j += 1) {
                    if (j != team.id) {
                        fn.addEdge(new FlowEdge(source, v, against[i][j]));
                        fn.addEdge(new FlowEdge(v, i, Double.POSITIVE_INFINITY));
                        fn.addEdge(new FlowEdge(v, j, Double.POSITIVE_INFINITY));
                        v += 1;
                    }
                }
            }
        }

        return fn;
    }

    private boolean isFullFromSource(FlowNetwork fn, int s) {
        for (FlowEdge fe : fn.adj(s)) {
            if (Math.abs(fe.flow() - fe.capacity()) > EPSILON) {
                return false;
            }
        }
        return true;
    }

    private void checkEliminated(Team team) {
        if (team.wins + team.remainings < maxWin) {
            ArrayList<String> subset = new ArrayList<>();
            subset.add(maxTeam);
            certificate.put(team.name, subset);
        }
        else {
            FlowNetwork fn = createFlowNetwork(team);
            int s = fn.V() - 2;
            int t = fn.V() - 1;
            FordFulkerson ff = new FordFulkerson(fn, s, t);
            if (isFullFromSource(fn, s)) {
                certificate.put(team.name, null);
            }
            else {
                ArrayList<String> subset = new ArrayList<>();
                for (int i = 0; i < numOfTeams; i += 1) {
                    if (ff.inCut(i)) {
                        subset.add(teams.get(i).name);
                    }
                }
                certificate.put(team.name, subset);
            }
        }
    }

    /** number of teams */
    public int numberOfTeams() {
        return numOfTeams;
    }

    /** all teams */
    public Iterable<String> teams() {
        ArrayList<String> allTeams = new ArrayList<>();
        for (Team t : teams) {
            allTeams.add(t.name);
        }
        return allTeams;
    }

    /** number of wins for given teams */
    public int wins(String team) {
        Team currentTeam = teamCheck(team);
        return currentTeam.wins;
    }

    /** number of losses for given team */
    public int losses(String team) {
        Team currentTeam = teamCheck(team);
        return currentTeam.losses;
    }

    /** number of remaining games for given team */
    public int remaining(String team) {
        Team currentTeam = teamCheck(team);
        return currentTeam.remainings;
    }

    /** number of remaining games between team1 and team2 */
    public int against(String team1, String team2) {
        Team currentTeam1 = teamCheck(team1);
        Team currentTeam2 = teamCheck(team2);
        return against[currentTeam1.id][currentTeam2.id];
    }

    /** is given team eliminated? */
    public boolean isEliminated(String team) {
        Team currentTeam = teamCheck(team);
        if (!certificate.containsKey(team)) {
            checkEliminated(currentTeam);
        }
        return certificate.get(team) != null;
    }

    /** subset R of teams that eliminated given team; null if not eliminated */
    public Iterable<String> certificateOfElimination(String team) {
        Team currentTeam = teamCheck(team);
        if (!certificate.containsKey(team)) {
            checkEliminated(currentTeam);
        }
        return certificate.get(currentTeam.name);
    }

    private Team teamCheck(String t) {
        if (t == null) {
            throw new IllegalArgumentException("No null!");
        }
        for (Team teamInTheGame : teams) {
            if (teamInTheGame.name.equals(t)) {
                return teamInTheGame;
            }
        }
        throw new IllegalArgumentException("No this team!");
    }

    /**  */
    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class BaseballElimination {
    private static int SOURCE = 0;
    private static int SINK = 1;
    private int N;
    private int[] W, L, R; // wins, losses, remaining games
    private int[][] G; // games left to play
    private String[] T; // teams
    private String leader;
    private HashMap<String, Integer> teamIndex = new HashMap<String, Integer>();

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In file = new In(filename);
        N = file.readInt();

        W = new int[N];
        L = new int[N];
        R = new int[N];

        G = new int[N][N];

        T = new String[N];

        int i = 0;
        int maxW = Integer.MIN_VALUE;

        while (i < N) {
            T[i] = file.readString();
            teamIndex.put(T[i], i);

            W[i] = file.readInt();
            L[i] = file.readInt();
            R[i] = file.readInt();

            for (int j = 0; j < N; ++j)
                G[i][j] = file.readInt();

            if (W[i] > maxW) {
                leader = T[i];
                maxW = W[i];
            }

            ++i;
        }
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        checkTeam(team1);
        checkTeam(team2);
        return G[teamIndex.get(team1)][teamIndex.get(team2)];
    }

    public Iterable<String> certificateOfElimination(String team) {
        checkTeam(team);

        Set<String> set = new HashSet<String>();

        if (isTriviallyEliminated(team)) {
            set.add(leader);
            return set;
        }

        FlowNetwork fn = getFlowNetwork(team);
        FordFulkerson ff = getFordFulkerson(fn);

        for (FlowEdge edge : fn.adj(SOURCE))
            if (edge.flow() < edge.capacity())
                for (String t : teams()) {
                    int teamVertex = getTeamVertex(teamIndex.get(t));
                    if (ff.inCut(teamVertex))
                        set.add(t);
                }

        if (set.isEmpty())
            return null;

        return set;
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        checkTeam(team);
        return isTriviallyEliminated(team) || isNonTriviallyEliminated(team);
    }

    // number of losses for given team
    public int losses(String team) {
        checkTeam(team);
        return L[teamIndex.get(team)];
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);

        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");

                for (String t : division.certificateOfElimination(team))
                    StdOut.print(t + " ");
                StdOut.println("}");

            } else
                StdOut.println(team + " is not eliminated");
        }
    }

    // number of teams
    public int numberOfTeams() {
        return N;
    }

    // number of remaining games for given team
    public int remaining(String team) {
        checkTeam(team);
        return R[teamIndex.get(team)];
    }

    // all teams
    public Iterable<String> teams() {
        return Arrays.asList(T);
    }

    // number of wins for given team
    public int wins(String team) {
        checkTeam(team);
        return W[teamIndex.get(team)];
    }

    private void checkTeam(String team) {
        if (team == null || !teamIndex.containsKey(team))
            throw new IllegalArgumentException();
    }

    private FlowNetwork getFlowNetwork(String team) {
        FlowNetwork fn = new FlowNetwork(2 + N * (N - 1) / 2 + N);
        int gameVertex = 2;
        int x = teamIndex.get(team);

        for (int i = 0; i < N; ++i) {

            if (i == x)
                continue;

            for (int j = i + 1; j < N; ++j) {

                if (j == x)
                    continue;

                int team1Vertex = getTeamVertex(i);
                int team2Vertex = getTeamVertex(j);

                FlowEdge edge = new FlowEdge(SOURCE, gameVertex, G[i][j]);
                fn.addEdge(edge);

                edge = new FlowEdge(gameVertex, team1Vertex, Double.POSITIVE_INFINITY);
                fn.addEdge(edge);

                edge = new FlowEdge(gameVertex, team2Vertex, Double.POSITIVE_INFINITY);
                fn.addEdge(edge);

                ++gameVertex;
            }
        }

        int wx = wins(team);
        int rx = remaining(team);

        for (int i = 0; i < N; ++i) {

            if (i == x)
                continue;

            int capacity = wx + rx - wins(T[i]);

            if (capacity < 0)
                capacity = 0;

            int teamVertex = getTeamVertex(i);

            fn.addEdge(new FlowEdge(teamVertex, SINK, capacity));
        }

        return fn;
    }

    private FordFulkerson getFordFulkerson(FlowNetwork network) {
        return new FordFulkerson(network, SOURCE, SINK);
    }

    private int getTeamVertex(int i) {
        return 2 + N * (N - 1) / 2 + i;
    }

    private boolean isNonTriviallyEliminated(String team) {
        FlowNetwork fn = getFlowNetwork(team);
        getFordFulkerson(fn);

        for (FlowEdge edge : fn.adj(SOURCE))
            if (edge.flow() < edge.capacity())
                return true;

        return false;
    }

    private boolean isTriviallyEliminated(String team) {
        if (team.equals(leader))
            return false;

        if (wins(team) + remaining(team) < wins(leader))
            return true;

        return false;
    }
}
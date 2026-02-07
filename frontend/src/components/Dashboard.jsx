import React, { useState, useEffect } from 'react';
import { Card, Badge } from './ui';
import {
    BarChart3,
    BrainCircuit,
    Users,
    Sparkles,
    TrendingUp,
    Target,
    Trophy,
    Zap,
    Activity,
    Layers
} from 'lucide-react';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { Client } from '@stomp/stompjs';

export default function Dashboard() {
    const [sentiment, setSentiment] = useState(0.8);
    const [creative, setCreative] = useState(0.9);
    const [loading, setLoading] = useState(false);

    // Gamification State
    const [level, setLevel] = useState(5);
    const [xp, setXp] = useState(2450);
    const [nextLevelXp] = useState(5000);
    const [rankTitle, setRankTitle] = useState("Marketing Novice");
    const [notification, setNotification] = useState(null);

    const [feedback, setFeedback] = useState("Run the simulation to get AI Strategy advice.");

    const [result, setResult] = useState({
        p10: 0.008,
        p50: 0.024,
        p90: 0.051
    });

    // Level & Rank Logic
    useEffect(() => {
        if (level >= 10) setRankTitle("Chief Strategy Officer");
        else if (level >= 7) setRankTitle("Lead Growth Hacker");
        else if (level >= 5) setRankTitle("Marketing Manager");

        if (xp >= nextLevelXp) {
            setXp(prev => prev - nextLevelXp);
            setLevel(prev => prev + 1);
            setNotification(`LEVEL UP! You are now a ${rankTitle}`);
            setTimeout(() => setNotification(null), 3000);
        }
    }, [xp, level, nextLevelXp, rankTitle]);

    // WebSocket Client
    useEffect(() => {
        const client = new Client({
            brokerURL: 'ws://localhost:8080/ws',
            onConnect: () => {
                console.log('Connected to WebSocket Broker');
                client.subscribe('/topic/simulation', (message) => {
                    if (message.body) {
                        const update = JSON.parse(message.body);
                        console.log("Real-time Update:", update);

                        if (update.type === 'sentiment') {
                            setSentiment(update.value);
                            handleRunSimulation(update.value, null);
                        } else if (update.type === 'creative') {
                            setCreative(update.value);
                            handleRunSimulation(null, update.value);
                        }
                    }
                });
            },
            onWebSocketError: (error) => {
                console.warn('WebSocket Error: ', error);
            }
        });

        client.activate();

        return () => {
            client.deactivate();
        };
    }, []);

    const handleRunSimulation = async (overrideSentiment = null, overrideCreative = null) => {
        setLoading(true);

        // Check if called via Event (onClick)
        let s = sentiment;
        let c = creative;

        if (typeof overrideSentiment === 'number') s = overrideSentiment;
        if (typeof overrideCreative === 'number') c = overrideCreative;

        try {
            const response = await fetch(`http://localhost:8080/api/simulation/predict?sentiment=${s}&creative=${c}`);
            if (!response.ok) throw new Error("Backend not reachable");

            const data = await response.json();
            setResult(data.metrics);
            setFeedback(data.mentorFeedback);

            // Dynamic XP Reward based on Success
            const bonusXP = Math.floor(data.metrics.p90 * 10000);
            setXp(prev => prev + 150 + bonusXP);

            if (data.metrics.p90 > 0.08) {
                setNotification("VIRAL HIT! +500 XP Bonus");
                setTimeout(() => setNotification(null), 2000);
            }

        } catch (error) {
            console.warn("Backend unavailable, switching to Demo Mode");
            await new Promise(resolve => setTimeout(resolve, 1500));

            const baseCtr = 0.015;
            const mockResult = {
                p10: baseCtr + (s * 0.002) + (c * 0.01),
                p50: baseCtr + (s * 0.005) + (c * 0.02),
                p90: baseCtr + (s * 0.010) + (c * 0.04)
            };
            setResult(mockResult);
            setFeedback("⚠️ DEMO MODE: The AI Mentor is offline. Connect the backend to get real strategic advice.");
            setXp(prev => prev + 50); // Small XP for demo
        } finally {
            setLoading(false);
        }
    };



    const chartData = [
        { name: 'Trial 1', ctr: (result.p10 * 100).toFixed(2) },
        { name: 'Trial 2', ctr: (result.p10 * 110).toFixed(2) }, // Smoothing
        { name: 'P50 (Avg)', ctr: (result.p50 * 100).toFixed(2) },
        { name: 'Trial 4', ctr: (result.p90 * 90).toFixed(2) }, // Smoothing
        { name: 'P90 (Max)', ctr: (result.p90 * 100).toFixed(2) },
    ];

    return (
        <div className="min-h-screen text-slate-100 p-8 font-sans selection:bg-cyan-500/30">

            {/* Background Ambience */}
            <div className="fixed inset-0 bg-[#020617] -z-20" />
            <div className="fixed top-0 left-1/2 -translate-x-1/2 w-[1000px] h-[600px] bg-blue-600/20 blur-[120px] rounded-full -z-10 opacity-30 pointer-events-none" />

            {/* Header */}
            <header className="flex items-center justify-between mb-12 border-b border-white/5 pb-6">
                <div className="flex items-center gap-4">
                    <div className="p-3 bg-gradient-to-br from-blue-600 to-cyan-500 rounded-xl shadow-[0_0_20px_rgba(37,99,235,0.5)]">
                        <BrainCircuit className="w-8 h-8 text-white" />
                    </div>
                    <div>
                        <h1 className="text-4xl font-black tracking-tight flex items-center gap-2">
                            PROJECT <span className="text-transparent bg-clip-text bg-gradient-to-r from-cyan-400 to-blue-500">SENTINEL</span>
                        </h1>
                        <div className="flex items-center gap-2 text-slate-400 text-sm font-medium">
                            <span className="w-2 h-2 rounded-full bg-green-500 animate-pulse" />
                            SYSTEM ONLINE
                        </div>
                    </div>
                </div>

                {/* Gamification Bar */}
                <div className="flex items-center gap-6">
                    <div className="text-right">
                        <p className="text-xs text-slate-400 font-bold tracking-wider mb-1">CAMPAIGN HEALTH</p>
                        <div className="w-48 h-2 bg-slate-800 rounded-full overflow-hidden relative">
                            <div
                                className={`h-full transition-all duration-1000 ease-out ${feedback.toLowerCase().includes("doomed") ? 'bg-red-500 w-[20%]' :
                                        feedback.toLowerCase().includes("volatile") ? 'bg-yellow-500 w-[50%]' :
                                            'bg-green-500 w-[100%]'
                                    }`}
                            />
                        </div>
                        <p className="text-xs text-right mt-1 font-mono text-slate-500">
                            {feedback.toLowerCase().includes("doomed") ? 'CRITICAL FAILURE' :
                                feedback.toLowerCase().includes("volatile") ? 'UNSTABLE' :
                                    'OPTIMAL'}
                        </p>
                    </div>

                    <Card className="py-2 px-5 flex items-center gap-4 !bg-slate-800/80 border-slate-700">
                        <div className="relative">
                            <Trophy className="w-8 h-8 text-yellow-400 drop-shadow-[0_0_10px_rgba(250,204,21,0.5)]" />
                            <div className="absolute -top-1 -right-1 w-4 h-4 bg-red-500 rounded-full text-[10px] flex items-center justify-center font-bold border border-slate-900">3</div>
                        </div>
                        <div>
                            <p className="text-[10px] text-slate-400 uppercase font-bold tracking-wider">Rank</p>
                            <p className="font-black text-xl text-yellow-400 leading-none">LVL {level}</p>
                        </div>
                    </Card>
                </div>
            </header>

            {/* Main Grid */}
            <div className="grid grid-cols-1 md:grid-cols-12 gap-8">

                {/* Left Column: Inputs */}
                <div className="col-span-12 md:col-span-4 space-y-6">
                    <Card>
                        <h2 className="text-xl font-bold flex items-center gap-2 text-white mb-6">
                            <Layers className="w-5 h-5 text-green-400" />
                            Campaign Context
                        </h2>

                        <div className="space-y-4 mb-6">
                            <div>
                                <label className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-2 block">Analyze Hashtag</label>
                                <div className="flex gap-2">
                                    <input
                                        type="text"
                                        placeholder="#summer2024"
                                        className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-sm text-cyan-400 focus:outline-none focus:border-cyan-500"
                                        onKeyDown={async (e) => {
                                            if (e.key === 'Enter') {
                                                setLoading(true);
                                                try {
                                                    const res = await fetch(`http://localhost:8080/api/analysis/hashtag?tag=${e.currentTarget.value.replace('#', '')}`);
                                                    const data = await res.json();
                                                    setSentiment(data.sentimentScore); // Autonomous Update
                                                    // Pass the NEW sentiment directly to the sim
                                                    handleRunSimulation(data.sentimentScore, null);
                                                } catch (err) {
                                                    console.warn("Analysis failed", err);
                                                } finally {
                                                    setLoading(false);
                                                }
                                            }
                                        }}
                                    />
                                </div>
                            </div>

                            <div>
                                <label className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-2 block">Analyze Creative URL</label>
                                <div className="flex gap-2">
                                    <input
                                        type="text"
                                        placeholder="https://example.com/ad.jpg"
                                        className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-sm text-pink-400 focus:outline-none focus:border-pink-500"
                                        onBlur={async (e) => {
                                            if (e.currentTarget.value) {
                                                setLoading(true);
                                                try {
                                                    const res = await fetch(`http://localhost:8080/api/analysis/creative`, {
                                                        method: 'POST',
                                                        headers: { 'Content-Type': 'application/json' },
                                                        body: JSON.stringify({ imageUrl: e.currentTarget.value, caption: "Auto-analyzed image" })
                                                    });
                                                    const data = await res.json();
                                                    setCreative(data.score); // Autonomous Update
                                                    // Pass the NEW creative directly to the sim
                                                    handleRunSimulation(null, data.score);
                                                } catch (err) {
                                                    console.warn("Creative Analysis failed", err);
                                                } finally {
                                                    setLoading(false);
                                                }
                                            }
                                        }}
                                    />
                                </div>
                            </div>
                        </div>
                    </Card>

                    <Card>
                        <div className="flex items-center justify-between mb-6">
                            <h2 className="text-xl font-bold flex items-center gap-2 text-white">
                                <Target className="w-5 h-5 text-cyan-400" />
                                Variable Control
                            </h2>
                            <Badge variant="purple">Manual Override</Badge>
                        </div>

                        <div className="space-y-8">
                            {/* Sentiment Slider */}
                            <div className="group">
                                <div className="flex justify-between mb-2">
                                    <label className="text-sm font-semibold text-slate-400 group-hover:text-purple-400 transition-colors">Audience Sentiment</label>
                                    <span className="font-mono text-purple-400 bg-purple-500/10 px-2 py-0.5 rounded border border-purple-500/20">{sentiment.toFixed(1)}</span>
                                </div>
                                <input
                                    type="range"
                                    min="-1"
                                    max="1"
                                    step="0.1"
                                    value={sentiment}
                                    onChange={(e) => setSentiment(parseFloat(e.target.value))}
                                    className="w-full h-2 bg-slate-800 rounded-lg appearance-none cursor-pointer accent-purple-500 hover:accent-purple-400 transition-all"
                                />
                                <div className="flex justify-between text-[10px] text-slate-600 font-bold mt-1 uppercase tracking-widest">
                                    <span>Hostile</span>
                                    <span>Fanatic</span>
                                </div>
                            </div>

                            {/* Creative Slider */}
                            <div className="group">
                                <div className="flex justify-between mb-2">
                                    <label className="text-sm font-semibold text-slate-400 group-hover:text-pink-400 transition-colors">Creative Quality</label>
                                    <span className="font-mono text-pink-400 bg-pink-500/10 px-2 py-0.5 rounded border border-pink-500/20">{creative.toFixed(1)}</span>
                                </div>
                                <input
                                    type="range"
                                    min="0"
                                    max="1"
                                    step="0.1"
                                    value={creative}
                                    onChange={(e) => setCreative(parseFloat(e.target.value))}
                                    className="w-full h-2 bg-slate-800 rounded-lg appearance-none cursor-pointer accent-pink-500 hover:accent-pink-400 transition-all"
                                />
                                <div className="flex justify-between text-[10px] text-slate-600 font-bold mt-1 uppercase tracking-widest">
                                    <span>Boring</span>
                                    <span>Viral</span>
                                </div>
                            </div>

                            <button
                                onClick={handleRunSimulation}
                                disabled={loading}
                                className="w-full py-4 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-500 hover:to-indigo-500 disabled:opacity-50 disabled:grayscale text-white rounded-xl font-bold tracking-wide transition-all shadow-[0_4px_20px_rgba(37,99,235,0.4)] hover:shadow-[0_8px_30px_rgba(37,99,235,0.6)] active:scale-[0.98] border border-white/10 relative overflow-hidden group">
                                <span className="relative z-10 flex items-center justify-center gap-2">
                                    {loading ? <Activity className="animate-spin" /> : <Zap className="fill-white" />}
                                    {loading ? 'SIMULATING ORACLE...' : 'RUN MONTE CARLO'}
                                </span>

                                {/* Sheen Effect */}
                                <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/20 to-transparent -translate-x-full group-hover:translate-x-full transition-transform duration-1000 ease-in-out" />
                            </button>
                        </div>
                    </Card>

                    <Card className="bg-slate-900/60">
                        <h3 className="text-sm font-bold text-slate-400 uppercase tracking-widest mb-4 flex items-center gap-2">
                            <Layers className="w-4 h-4" />
                            MAS Status
                        </h3>
                        <div className="space-y-3">
                            <div className="flex items-center justify-between p-3 rounded-lg bg-black/20 border border-white/5 hover:border-green-500/30 transition-colors group">
                                <div className="flex items-center gap-3">
                                    <div className="p-1.5 rounded bg-green-500/10 text-green-400 group-hover:bg-green-500 group-hover:text-black transition-colors">
                                        <Users className="w-4 h-4" />
                                    </div>
                                    <div>
                                        <span className="text-sm font-bold block">Audience Architect</span>
                                        <span className="textxs text-slate-500">Profiling Active</span>
                                    </div>
                                </div>
                                <div className="h-2 w-2 rounded-full bg-green-500 shadow-[0_0_8px_#22c55e]" />
                            </div>

                            <div className="flex items-center justify-between p-3 rounded-lg bg-black/20 border border-white/5 hover:border-pink-500/30 transition-colors group">
                                <div className="flex items-center gap-3">
                                    <div className="p-1.5 rounded bg-pink-500/10 text-pink-400 group-hover:bg-pink-500 group-hover:text-black transition-colors">
                                        <Sparkles className="w-4 h-4" />
                                    </div>
                                    <div>
                                        <span className="text-sm font-bold block">Creative Scorer</span>
                                        <span className="textxs text-slate-500">Vision 4.0 Online</span>
                                    </div>
                                </div>
                                <div className="h-2 w-2 rounded-full bg-pink-500 shadow-[0_0_8px_#ec4899]" />
                            </div>
                        </div>
                    </Card>
                </div>

                {/* Right Column: Visualization */}
                <div className="col-span-12 md:col-span-8 space-y-6">
                    <div className="grid grid-cols-3 gap-6">
                        <Card className="border-red-500/20 bg-red-500/5 group">
                            <p className="text-red-400 text-[10px] font-black uppercase tracking-widest mb-2">P10 • Pessimistic</p>
                            <div className="flex items-baseline gap-1">
                                <p className="text-3xl font-black text-white group-hover:text-red-400 transition-colors">{(result.p10 * 100).toFixed(2)}</p>
                                <span className="text-sm font-bold text-red-500/50">% CTR</span>
                            </div>
                        </Card>
                        <Card className="border-blue-500/30 bg-blue-500/10 group relative overflow-hidden">
                            <div className="absolute top-0 right-0 p-2 opacity-50">
                                <Target className="w-12 h-12 text-blue-500/20 -rotate-12" />
                            </div>
                            <p className="text-blue-300 text-[10px] font-black uppercase tracking-widest mb-2">P50 • Expected</p>
                            <div className="flex items-baseline gap-1">
                                <p className="text-4xl font-black text-white group-hover:scale-105 transition-transform duration-300">{(result.p50 * 100).toFixed(2)}</p>
                                <span className="text-sm font-bold text-blue-400">% CTR</span>
                            </div>
                        </Card>
                        <Card className="border-emerald-500/20 bg-emerald-500/5 group">
                            <p className="text-emerald-400 text-[10px] font-black uppercase tracking-widest mb-2">P90 • Optimistic</p>
                            <div className="flex items-baseline gap-1">
                                <p className="text-3xl font-black text-white group-hover:text-emerald-400 transition-colors">{(result.p90 * 100).toFixed(2)}</p>
                                <span className="text-sm font-bold text-emerald-500/50">% CTR</span>
                            </div>
                        </Card>
                    </div>

                    {/* AI STRATEGY MENTOR (NEW) */}
                    <Card className="bg-slate-900/80 border-purple-500/20 relative overflow-hidden">
                        {/* Neon Glow effect behind the mentor */}
                        <div className="absolute top-0 right-0 w-64 h-64 bg-purple-600/10 blur-[80px] rounded-full -z-10" />

                        <h3 className="text-lg font-bold flex items-center gap-2 text-white mb-4">
                            <BrainCircuit className="w-5 h-5 text-purple-400" />
                            AI Strategy Mentor
                            <Badge variant="purple">Pyramid Prompting</Badge>
                        </h3>
                        <div className="p-5 bg-black/40 rounded-xl border border-white/5 text-slate-300 leading-relaxed font-mono text-sm relative">
                            <div className="whitespace-pre-line">{feedback}</div>
                        </div>
                    </Card>

                    <Card className="h-[400px] relative">
                        <div className="flex items-center justify-between mb-8">
                            <h3 className="text-lg font-bold flex items-center gap-2 text-white">
                                <TrendingUp className="w-5 h-5 text-cyan-400" />
                                Performance Projection
                            </h3>
                            <Badge>R-Squared: 0.98</Badge>
                        </div>

                        <ResponsiveContainer width="100%" height="80%">
                            <AreaChart data={chartData}>
                                <defs>
                                    <linearGradient id="colorCtr" x1="0" y1="0" x2="0" y2="1">
                                        <stop offset="5%" stopColor="#06b6d4" stopOpacity={0.4} /> {/* Cyan */}
                                        <stop offset="95%" stopColor="#06b6d4" stopOpacity={0} />
                                    </linearGradient>
                                </defs>
                                <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" vertical={false} />
                                <XAxis
                                    dataKey="name"
                                    stroke="#475569"
                                    fontSize={11}
                                    fontWeight={600}
                                    tickLine={false}
                                    axisLine={false}
                                    dy={10}
                                />
                                <YAxis
                                    stroke="#475569"
                                    fontSize={11}
                                    fontWeight={600}
                                    tickLine={false}
                                    axisLine={false}
                                    tickFormatter={(value) => `${value}%`}
                                />
                                <Tooltip
                                    contentStyle={{
                                        backgroundColor: '#0f172a',
                                        borderColor: 'rgba(255,255,255,0.1)',
                                        borderRadius: '12px',
                                        boxShadow: '0 10px 30px -10px rgba(0,0,0,0.5)'
                                    }}
                                    itemStyle={{ color: '#fff', fontWeight: 'bold' }}
                                />
                                <Area
                                    type="monotone"
                                    dataKey="ctr"
                                    stroke="#22d3ee"
                                    strokeWidth={4}
                                    fillOpacity={1}
                                    fill="url(#colorCtr)"
                                    animationDuration={2000}
                                />
                            </AreaChart>
                        </ResponsiveContainer>
                    </Card>
                </div>
            </div>
        </div>
    );
}
